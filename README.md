# springboot admin

# 说明

- ### 可自行构建：
  - git clone
  - mvn clean package -Dmaven.test.skip=true
  - cd target
  - java -Dspring.profiles.active=test -jar octopus-0.0.1-SNAPSHOT.jar

- ### 可使用dist目录下已有的jar包直接启动服务
  - cd dist
  - java -Dspring.profiles.active=test -jar octopus.jar
