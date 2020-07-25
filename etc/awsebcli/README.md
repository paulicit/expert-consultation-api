# Quick and easy deploy to aws elastic beanstalk for testing

## Install aws ebc cli

``` shell
pip install awsebcli --upgrade
```

## Set the authentication details as environment variables:
``` shell
export AWS_ACCESS_KEY_ID='<key_id>'
export AWS_SECRET_ACCESS_KEY='<secret_key>'
export AWS_DEFAULT_REGION='<region>
```

## Initialize the beanstalk app
``` shell
eb init ExpertConsultationApi \
    --region ${AWS_DEFAULT_REGION} \
    --platform "corretto-11"
```

## Build the package and configure it for deploy
``` shell
mvn package -DskipTests
export DEPLOY_ZIP='deploy:\n  artifact: target/expert-consultation-0.0.1-SNAPSHOT.jar'
echo -e $DEPLOY_ZIP >> .elasticbeanstalk/config.yml

```
## Create the beanstalk environment
``` shell
# define the variables/credentials used as application properties
export ENVVARS="\
SERVER_PORT=5000,\
SPRING_PROFILES_ACTIVE=production,\
AWS_REGION=${AWS_DEFAULT_REGION},\
AWS_ACCESS_ID=${AWS_ACCESS_KEY_ID},\
AWS_SECRET_KEY=${AWS_SECRET_ACCESS_KEY},\
AWS_DOCUMENT_BUCKET=expert-consultation-documents\
"
# define the db user and password
export DB_USER=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
export DB_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)

eb create ExpertConsultationApi-staging \
    --region ${AWS_DEFAULT_REGION} \
    --platform "corretto-11" \
    --single \
    --sample \
    --cname expert-consultation-api-staging \
    --database \
    --database.engine postgres \
    --database.instance db.t2.micro \
    --database.version 12.2 \
    --database.size 5 \
    --database.password $DB_PWD \
    --database.username $DB_USER \
    --envvars $ENVVARS \
    --debug
```
## Deploy the package

``` shell
eb deploy
```

## Check the deployment
The api should be available at: http://expert-consultation-api-staging.${AWS_DEFAULT_REGION}.elasticbeanstalk.com/
