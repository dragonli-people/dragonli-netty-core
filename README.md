# dragonli-netty-core
* 任何想对外提供websocket服务的服务，均可引入本库项目，并以一行代码启动netty服务
* 本项目不引入任何配置中心里的配置，所有配置均由引入本项目的服务在启动时提供
* 如果希望向用户提供websocket服务，通常还需要在本项目的基础上作一定的封装，详见 dragonli-netty-service 项目