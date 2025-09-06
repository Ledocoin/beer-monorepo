# 📜 README

## EN — Introduction

This repository contains a **BeerStack** demo project — a microservices-based system that manages **beers, stores, storages, and employees**. It demonstrates modern backend architecture with **Spring Boot, PostgreSQL, MongoDB, Redis, Kafka**, and container orchestration via **Docker Compose**.

The system consists of two services:

-   **Main Service** — handles beers, stores, storages, and cross-service queries.
    
-   **Employee Service** — manages employees, connected to stores, with caching enabled.
    

The goal is to showcase domain-driven design, service-to-service communication (via REST and Kafka), persistence with relational & NoSQL databases, and production-like patterns suitable for portfolio and interviews.

----------

## EN — System Overview

### Services

-   **service-main**
    
    -   Manages beers, stores, storages
        
    -   Integrates with **Employee Service** via Feign client
        
    -   Publishes and consumes **Kafka events**
        
    -   Stores domain data in **PostgreSQL** (Liquibase migrations included)
        
    -   Exposes REST endpoints for CRUD and search queries
        
-   **service-employee**
    
    -   Manages employees and their relation to stores
        
    -   Uses **MongoDB** for persistence
        
    -   Uses **Redis** for caching (employee lookups per store and employee ID)
        
    -   Exposes REST API to the main service
        

### Infrastructure (via Docker Compose)

-   **PostgreSQL** — beer/store/storage persistence
    
-   **MongoDB** — employee persistence
    
-   **Redis** — caching layer for employees
    
-   **Kafka + Zookeeper** — async communication between services
    
-   **Service Main** on port `8080`
    
-   **Service Employee** on port `8081`
    

----------

## EN — Functional Features

-   Beer management: create, update, patch, delete, search by criteria
    
-   Store management: CRUD + fetch store with employees
    
-   Storage management: manage inventory, bind beers to stores
    
-   Employee management: CRUD operations, store-based grouping
    
-   Cross-service queries:
    
    -   Register employees into a store
        
    -   Delete store and cascade employee removal
        
    -   Search for beers/stores by multiple conditions
        
-   Kafka flows: async store & employee creation
    
-   Redis cache: fast lookups for employees
    

----------

## EN — Requirements

**Functional**

-   REST API with JSON input/output
    
-   Validation and exception handling
    
-   Inter-service communication via REST + Kafka
    
-   Cache eviction on updates/deletes
    

**Non-functional**

-   Containerized environment with Docker Compose
    
-   Database migrations via Liquibase
    
-   Integration tests with Testcontainers
    
-   Modular monorepo structure
    

----------

## EN — Run Instructions

1.  Clone the repository
    
2.  Start the environment:
    
    `docker-compose up --build` 
    
3.  Main endpoints available at [http://localhost:8080](http://localhost:8080)
    
4.  Employee endpoints at [http://localhost:8081](http://localhost:8081)