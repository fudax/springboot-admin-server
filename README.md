# springboot admin

# 说明

- ### 可自行构建：
  - git clone https://github.com/fudax/octopus.git
  - 修改application.yml等文件，配置邮件通知相关参数
  - mvn clean package -Dmaven.test.skip=true
  - cd target
  - java -Dspring.profiles.active=test -jar octopus-0.0.1-SNAPSHOT.jar

- ### 可使用dist目录下已有的jar包直接启动服务（无邮件通知服务）
  - cd dist
  - java -Dspring.profiles.active=test -jar octopus.jar
