#!/bin/bash

# Script to apply all Kubernetes YAML files in the correct order
# This ensures dependencies are met (e.g., namespace before other resources)

set -e  # Exit on any error

echo "🚀 Starting Kubernetes deployment for CinemaAbyss..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed or not in PATH"
    exit 1
fi

# Check if minikube is running
if ! kubectl cluster-info &> /dev/null; then
    print_error "Kubernetes cluster is not accessible. Make sure minikube is running."
    exit 1
fi

print_status "Kubernetes cluster is accessible"

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
K8S_DIR="$SCRIPT_DIR/src/kubernetes"

# Check if kubernetes directory exists
if [ ! -d "$K8S_DIR" ]; then
    print_error "Kubernetes directory not found: $K8S_DIR"
    exit 1
fi

print_status "Found Kubernetes directory: $K8S_DIR"

# Function to apply YAML file with error handling
apply_yaml() {
    local file="$1"
    local description="$2"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    print_status "Applying $description..."
    if kubectl apply -f "$file"; then
        print_success "$description applied successfully"
    else
        print_error "Failed to apply $description"
        return 1
    fi
}

# Function to wait for pods to be ready
wait_for_pods() {
    local namespace="$1"
    local selector="$2"
    local timeout="${3:-300}"  # Default 5 minutes
    
    print_status "Waiting for pods with selector '$selector' in namespace '$namespace' to be ready..."
    
    if kubectl wait --for=condition=ready pod -l "$selector" -n "$namespace" --timeout="${timeout}s"; then
        print_success "Pods with selector '$selector' are ready"
    else
        print_warning "Timeout waiting for pods with selector '$selector' to be ready"
        return 1
    fi
}

# Apply YAML files in dependency order
echo ""
print_status "=== Phase 1: Core Infrastructure ==="

# 1. Namespace (must be first)
apply_yaml "$K8S_DIR/namespace.yaml" "Namespace"

# 2. Secrets and ConfigMaps
apply_yaml "$K8S_DIR/secret.yaml" "Secrets"
apply_yaml "$K8S_DIR/configmap.yaml" "ConfigMap"
apply_yaml "$K8S_DIR/dockerconfigsecret.yaml" "Docker Config Secret"
apply_yaml "$K8S_DIR/postgres-init-configmap.yaml" "PostgreSQL Init ConfigMap"

echo ""
print_status "=== Phase 2: Database and Message Queue ==="

# 3. PostgreSQL
apply_yaml "$K8S_DIR/postgres.yaml" "PostgreSQL"
print_status "Waiting for PostgreSQL to be ready..."
wait_for_pods "cinemaabyss" "app=postgres" 120

# 4. ZooKeeper and Kafka
apply_yaml "$K8S_DIR/kafka/kafka.yaml" "ZooKeeper and Kafka"
print_status "Waiting for ZooKeeper to be ready..."
wait_for_pods "cinemaabyss" "app=zookeeper" 120

# Check if Kafka is having cluster ID issues and fix them
print_status "Checking Kafka status..."
if ! wait_for_pods "cinemaabyss" "app=kafka" 60; then
    print_warning "Kafka failed to start, checking for cluster ID issues..."
    if kubectl logs -l app=kafka -n cinemaabyss --tail=10 | grep -q "InconsistentClusterIdException"; then
        print_status "Detected Kafka cluster ID mismatch. Cleaning up and recreating..."
        kubectl delete statefulset kafka -n cinemaabyss
        kubectl delete pvc kafka-data -n cinemaabyss
        sleep 5
        apply_yaml "$K8S_DIR/kafka/kafka.yaml" "Kafka (recreated)"
        print_status "Waiting for Kafka to be ready after recreation..."
        wait_for_pods "cinemaabyss" "app=kafka" 180
    else
        print_error "Kafka failed for unknown reasons. Check logs manually."
        exit 1
    fi
fi

echo ""
print_status "=== Phase 3: Application Services ==="

# 5. Application services (can be applied in parallel)
apply_yaml "$K8S_DIR/monolith.yaml" "Monolith Service"
apply_yaml "$K8S_DIR/movies-service.yaml" "Movies Service"
apply_yaml "$K8S_DIR/events-service.yaml" "Events Service"
apply_yaml "$K8S_DIR/proxy-service.yaml" "Proxy Service"

echo ""
print_status "=== Phase 4: Networking ==="

# 6. Ingress
apply_yaml "$K8S_DIR/ingress.yaml" "Ingress"

echo ""
print_status "=== Phase 5: Verification ==="

# Wait for all application pods to be ready
print_status "Waiting for all application services to be ready..."
wait_for_pods "cinemaabyss" "app=monolith" 120
wait_for_pods "cinemaabyss" "app=movies-service" 120
wait_for_pods "cinemaabyss" "app=events-service" 120
wait_for_pods "cinemaabyss" "app=proxy-service" 120

echo ""
print_success "=== Deployment Complete! ==="

# Show final status
echo ""
print_status "Final pod status:"
kubectl get pods -n cinemaabyss

echo ""
print_status "Services:"
kubectl get services -n cinemaabyss

echo ""
print_status "Ingress:"
kubectl get ingress -n cinemaabyss

echo ""
print_success "🎉 CinemaAbyss deployment completed successfully!"
print_status "You can now access your services through the ingress or port-forwarding."

# Show useful commands
echo ""
print_status "Useful commands:"
echo "  View all pods:     kubectl get pods -n cinemaabyss"
echo "  View logs:         kubectl logs -f <pod-name> -n cinemaabyss"
echo "  Port forward:      kubectl port-forward svc/proxy-service 8080:8080 -n cinemaabyss"
echo "  Delete namespace:  kubectl delete namespace cinemaabyss"
