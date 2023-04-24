# ChatGPT国内版
通过SSE建立前后端长链接，用于实现ChatGPT官网逐字输出的效果

# 部署
1. 将ChatConstant类下的CHAT_GPT_KEY属性设置为自己的ChatGPT账号对应的密钥
2. 将ProxyConstant类下的PROXY_PORT属性改为你挂的代理的端口【如果是国外服务器，则可去掉代理】
3. 执行ChatGptApplication
4. 访问http://localhost:8080/nokey.html

# 演示
我在自己的服务器上部署了个，通过下面这个地址即可访问，大家悠着点玩，服务器比较辣鸡，经不起折腾
http://121.196.223.15:8080/nokey.html
