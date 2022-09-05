|-- attribute
|   `-- Attributes.java 属性
|-- client
|   |-- NettyClient.java 对客户端进行组装
|   `-- handler
|       |-- LoginResponseHandler.java 登陆回复回复处理器
|       `-- MessageResponseHandler.java 消息回复处理器
|-- codec
|   |-- PacketDecoder.java 解码器
|   |-- PacketEncoder.java 编码器
|   `-- Spliter.java 分割器
|-- protocol
|   |-- Packet.java
|   |-- PacketCodec.java
|   |-- command
|   |   `-- Command.java
|   |-- request
|   |   |-- LoginRequestPacket.java 登陆请求体
|   |   `-- MessageRequestPacket.java 消息请求体
|   `-- response
|       |-- LoginResponsePacket.java 登陆回复消息体
|       `-- MessageResponsePacket.java 消息回复体
|-- readme.md
|-- serialize
|   |-- Serializer.java 序列化
|   |-- SerializerAlogrithm.java 序列化
|   `-- impl
|       `-- JSONSerializer.java
|-- server
|   |-- NettyServer.java
|   `-- handler
|       |-- AuthHandler.java
|       |-- LoginRequestHandler.java
|       `-- MessageRequestHandler.java
|-- session
|   `-- Session.java
`-- util
|-- LoginUtil.java
`-- SessionUtil.java

