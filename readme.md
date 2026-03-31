# 🚀 API Spring Boot com Docker & Java 21
Este projeto é uma API desenvolvida em Spring Boot 3 utilizando Java 21, configurada para rodar em containers Docker com banco de dados Postgres.

## 📦 Como Rodar o Projeto (Desenvolvimento)

É possível rodar diretamente pelo IntelliJ, basta rodar o comando abaixo para subir o banco. 
```
docker-compose up -d
```

## 🚢 Deploy e Containerização (Production-Ready)
Para levar a aplicação para produção ou deploy, utilizamos o Dockerfile em conjunto com o Docker Compose. Esse processo empacota todo o ecossistema necessário:

Maven & Java 21: O projeto utiliza uma estratégia de Multi-stage Build. Primeiro, um container com Maven e Java 21 compila o código.

JRE 21: O artefato final (.jar) é movido para um container leve contendo apenas o JRE 21 (Runtime), garantindo performance e segurança.

PostgreSQL: O banco de dados é provisionado como um serviço irmão, garantindo que a aplicação sempre tenha onde persistir os dados.

Comandos para Deploy:

```
// Cria o jar do projeto
mvn clean install -Dskiptests
```

```
// Cria a rede para que o app se comunique com o banco dentro do conteiner
docker network create api-network
```

```
// Sobe o banco
docker-compose up -d
```

```
// Sobe a aplicação
docker-compose -f docker-compose.deploy.yml up --build -d
// Esse mesmo comando pode ser rodado para atualizar a aplicação caso alguma alteração tenha sido feita
```

## 🔍 Health Check (Verificação de Saúde)
Existe um endpoint manual para garantir que a API está respondendo e "viva" no servidor:

URL: http://localhost:8080/health

## 📖 Documentação Swagger (OpenAPI)
A API utiliza o SpringDoc OpenAPI para gerar documentação automática que pode ser testada direto no navegador.

Com a aplicação rodando, acesse: http://localhost:8080/swagger-ui/index.html

Lá você encontrará detalhes de todos os endpoints, tipos de retorno (Records) e poderá testar as chamadas.

## 🚀 Integração com Bruno
Abra o Bruno e importe via url usando o link http://localhost:8080/v3/api-docs.

Isso vai trazer automaticamente as requisições do sistema.