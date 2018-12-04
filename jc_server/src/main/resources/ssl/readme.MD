1.生成Netty服务端私钥和证书仓库
keytool -genkey -alias server_cert -keypass 123456 -keyalg RSA -keysize 1024 -validity 365 -keystore ./server.jck -storepass 123456 -dname "CN=localhost"


2.生成Netty服务端自签名证书
keytool -export -alias server_cert -keystore server.jck -storepass 123456 -file ./server.cer


3.生成客户端的密钥对和证书仓库，用于将服务端的证书保存到客户端的授信证书仓库中
keytool -genkey -alias client_cert -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass 123456 -storepass 123456 -keystore ./client.jck


4.将服务端证书导入到客户端的证书仓库中
keytool -import -trustcacerts -alias securechat -file ./server.cer -storepass 123456 -keystore clinet.jck


5.生成客户端自签名证书
keytool -export -alias client_cert -keystore ./client.jks -storepass 123456 -file client.cer


6.将客户端的自签名证书导入到服务端的信任证书仓库中
keytool -import -trustcacerts -alias securechat -file ./client.cer -storepass 123456 -keystore ./server.jks