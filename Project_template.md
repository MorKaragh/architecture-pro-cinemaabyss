## Изучите [README.md](README.md) файл и структуру проекта.

## Задание 1

[Диаграмма C4 Containers](docs/C4_diagrams/C4_Containers.puml)


## Задание 2

### 1. Proxy

[Сервис](src/microservices/proxy/) реализован на "голой" java

### 2. Kafka

[Сервис](src/microservices/events/) реализован на Java + Spring Boot

тесты проходят

![скриншот вывыода тестов](docs/proofs/tests_output.png)

логи events service

![логи events](docs/proofs/consumer_producer_output.png)

топики Kafka после тестов

![скриншот kafka-ui](docs/proofs/kafka_ui.png)


## Задание 3

[Github actions](.github/workflows/) реализованы


![пайплайны](docs/proofs/pipelines.png)


### Proxy в Kubernetes

Система развернута в k8s

![get pods](docs/proofs/just_get_pod.png)
![get ingress](docs/proofs/just_get_ingress.png)

movies отвечает

![curl_movies](docs/proofs/curl_movies_k8s.png)

логи event service пишутся

![event service логи](docs/proofs/events_pod_logs.png)



## Задание 4

[Helm chart](src/kubernetes/helm/) реализован

всё устанавливается

![helm_install](docs/proofs/helm_install.png)

movies отвечает

![curl_movies](docs/proofs/curl_movies_k8s_after_helm.png)

тесты проходят

![tests_after_helm](docs/proofs/helm_tests_output.png)


# Задание 5

istio поставил, circuit breaker работает


![circuit breaker](docs/proofs/circuit_breaker.png)


![circuit breaker 2](docs/proofs/circuit_breaker_2.png)