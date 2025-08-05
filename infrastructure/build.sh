#!/usr/bin/env sh

PROJECT_FOLDER="${PWD}/.."
INFRA_FOLDER="${PWD}"
SCA_FOLDER="${PWD}/sca"
BONITA_ENVIRONMENT="presales"
ENV_FILE=${SCA_FOLDER}/.env-local-laurent

# cleanup
docker compose -f ${SCA_FOLDER}/docker-compose.yml --env-file ${ENV_FILE} -p bonita_sca down -v
docker image rm bonita_sca:1.0

# build
cd ${PROJECT_FOLDER} || exit
./mvnw bonita-project:install
./mvnw clean package \
-Pdocker \
-Dbonita.environment=${BONITA_ENVIRONMENT} \
-Ddocker.baseImageRepository=bonitasoft.jfrog.io/docker-releases/bonita-subscription \
-Ddocker.imageName=bonita_sca:1.0

# start
docker compose -f ${SCA_FOLDER}/docker-compose.yml --env-file ${ENV_FILE} -p bonita_sca up -d

# wait server started
${INFRA_FOLDER}/healthz.sh

# run IT
echo "run IT"
source ${ENV_FILE}
echo "running tests with BONITA_EXPOSED_PORT=${BONITA_EXPOSED_PORT} and EC2_PUBLIC_HOSTNAME=${EC2_PUBLIC_HOSTNAME}"
./mvnw clean verify -f ${PROJECT_FOLDER}/IT/pom.xml \
-Dbonita.url=http://${EC2_PUBLIC_HOSTNAME}:${BONITA_EXPOSED_PORT}/bonita