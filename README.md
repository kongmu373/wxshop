# wxshop
  本项目实现一个分布式的微型电商项目的后端实现，按照前端实现以及接口文档的后端实现，并对其微服务化，
  使用RPC调用，最终使用分布式方式部署该应用。
  
## 基本功能
- 基于手机验证码的登录（使用模拟服务）
- 买家
  - 编辑个人信息/收货地址
  - 商品浏览
  - 加购物车
  - 下单
  - 付款（使用模拟服务）
  - 查看物流信息（使用模拟服务）
  - 确认收货
- 卖家
  - 新建/修改店铺
  - 添加/编辑/删除宝贝
  - 查看订单
  - 发货

## Feature
- MyBatis Generator自动生成DAO代码
- 使用Shiro框架,ThreadLocal,AOP及Redis完成登录模块
- 配置Checkstyle,SpotBugs等代码检查工具，编写单元、集成测试，配置jacoco测试覆盖率报告完成自动化测试与持续集成
- RPC调用（Dubbo）+ 微服务化 实现订单模块
- 使用Spring AOP，添加统一的异常处理
- Docker 进行微服务部署
- 分布式部署 + Nginx负载均衡

## Requirements
+ jdk8
+ Maven
+ Docker

## Installation
1. 在阿里云上买一台ECS服务器

2. 部署模型
   1. 服务器对外暴露一个nginx(80port), 所有外部连接都经过nginx
      - /api      wxshop-main(8080port) wxshop-main2(8081port)
        - 共享redis维持登录状态(6379port)
        - RPC   wxshop-order(8082)
        - 
      - /static,/js,/css   直接放在服务器上(未来可以放在CDN上) 
      - MySQL(3306port)/zookeeper(2181port)       wxhop-main wxshop-order 都需要访问数据库与Zookeeper
3. 生产中不能使用root用户
4. 在服务器中安装需要的环境
    - jdk
      - 在 injdk.cn 寻找合适的jdk
      - `wget https://d2.injdk.cn/oracle/8/jdk-8u251-linux-x64.tar.gz`
      - `tar -zxvf jdk-xxx.tar.gz`
    - docker 
      - 直接google对应的 系统名 + docker  按步骤下载
      - https://docs.docker.com/engine/install/ubuntu/
    - yarn
      - ubuntu的安装步骤:https://linuxize.com/post/how-to-install-yarn-on-ubuntu-18-04/
    - node,npm(你可能需要upgrade)
    - 打包前端项目
      - `cd 到前端的目录 `
      - `npm install`
      - `yarn build`(如果缺失依赖包, `npm install xxx`)
      - 运行完毕后，`ls build` 就可以看到压缩的前端项目
    - redis && zookeeper && mysql && nginx
      - `docker pull redis `
      - `docker pull zookeeper `
      - `docker pull mysql && docker pull nginx`
      
5. 启动需要的环境

```shell
docker run -d -v ~/wxshop-data:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=wxshop --name=wxshop-mysql mysql
docker run -p 6379:6379 -d redis
docker run -p 2181:2181 -d zookeeper
# 等待半分钟，等容器启动完毕
# 创建order数据库：
docker exec -it wxshop-mysql mysql -uroot -p123 -e 'create database if not exists `order`'
./mvnw install -DskipTests
./mvnw flyway:migrate -pl wxshop-main
./mvnw flyway:migrate -pl wxshop-order
docker build . -t wxshop-main
# /wxshop/wxshop-main
docker build . -t wxshop-order
# /wxshop/wxshop-order
```

6. 配置nginx 
    - 配置负载均衡
      - /api  转发到后端服务  8080端口
      - 静态资源
        nginx.conf
        
 ```
      events{                                       
      }                                             
      http {                                        
          upstream app {                            
              server 172.17.0.1:8080;               
              server 172.17.0.1:8082;               
          }                                         
                                                    
          server {                                  
              listen 80;                            
                                                    
              location /api {                       
                  proxy_pass http://app;            
              }                                     
                                                    
              location / {                          
                  root    /static;                  
                  autoindex on;                     
              }                                     
          }                                         
      }                                          
```

7. 启动程序
   1. 8080启动 8081启动
    
```shell

    # wxshop-main 
      # 配置/wxshop/wxshop-main/config/application.yml 对应的ip地址
      docker run -it -p 8080:8080 -v ~/wxshop/wxshop-main/config/application.yml:/app/application.yml wxshop-main
      docker run -it -p 8082:8080 -v ~/wxshop/wxshop-main/config/application.yml:/app/application.yml wxshop-main

    # wxshop-order
    # 配置/wxshop/wxshop-order/config/application.yml 对应的ip地址
      docker run -it -p 8081:8081 -v ~/wxshop/wxshop-order/config/application.yml:/app/application.yml wxshop-order
    # nginx 启动 其中~/build 为前端`yarn build`对应的静态资源
    docker run -d -p 5000:80 -v ~/build:/static -v ~/nginx-conf/nginx.conf:/etc/nginx/nginx.conf nginx

```
    
8. 然后就可以打开对应ip或域名地址的:5000访问该微店商城了!  

