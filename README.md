# DevOps CI/CD Pipeline for **Infinitum-Art** (deploy-backend-app)

This repository contains the backend application **Infinitum-Art** along with the complete **CI/CD DevOps pipeline** used to automate building, testing, analyzing, containerizing, and deploying the application.

---

## 📌 Project Overview

**Infinitum-Art** is a **Spring Boot** e-commerce platform for selling and exhibiting digital art.  
It manages:
- Artist accounts
- Artwork listings
- Customer accounts
- Orders and transactions

The main objective of this project was to build a **fully automated CI/CD pipeline** that deploys the backend application from **GitHub → Jenkins → Docker → Kubernetes** with integrated monitoring.

---

## 🧰 Technologies Used

| Purpose | Tool |
|--------|------|
| Source Control | GitHub |
| CI/CD Orchestration | Jenkins |
| Code Quality & Security | SonarQube |
| Containerization | Docker & Docker Hub |
| Deployment & Scaling | Kubernetes (AKS - Azure Kubernetes Service) |
| Monitoring & Visualization | Prometheus & Grafana |

---

## 🔄 DevOps Pipeline Workflow (Jenkins)

The pipeline is implemented in a **Jenkinsfile** and performs:

1. **Checkout Code** — Pulls source from the `develop` branch.
2. **Compile Application** — Uses Maven to compile the Spring Boot service.
3. **Run Unit Tests** — Executes `mvn test`.
4. **Package JAR** — Generates the production-ready `.jar` file.
5. **SonarQube Analysis** — Sends code to SonarQube to check:
   - Bugs
   - Vulnerabilities
   - Code Smells
6. **Build Docker Image** — Builds image based on `Dockerfile`.
7. **Tag & Push Image to Docker Hub**:
   - `anasselhadi850/tp-devops:latest`
   - `anasselhadi850/tp-devops:<BUILD_NUMBER>`
8. **Pipeline Auto Trigger** — Webhook triggers on each push to `develop`.

---

## ☸ Kubernetes Deployment (AKS)

The application is deployed on **Azure Kubernetes Service**.

### Components:
| Component | Description |
|----------|-------------|
| Backend Deployment | Runs 2 replicas for high availability |
| PostgreSQL Deployment | Runs database backing the application |
| Service (LoadBalancer) | Exposes backend externally |

### Key Manifest: `deployment.yaml`
- Runs **2 pods** (`replicas: 2`)
- Uses image: `anasselhadi850/tp-devops:latest`
- Exposes port **8080**

### Service Example (Implied):
A `LoadBalancer` service exposes the app publicly (ex: `http://172.212.15.59`).

---

## 🐳 Docker Image

To run the backend locally using Docker:

```bash
docker run -p 8080:8080 anasselhadi850/tp-devops:latest
