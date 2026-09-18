# Jpos-glue

[English](README.md) · **简体中文**

> 英文版是规范版本。本页与 [README.md](README.md) 不一致时，以英文版为准。

<!-- translation-of: README.md sha256:f99bbd2882fc9251 -->

面向 Linux 上存量设备（RS-232/TCP）的净室（clean-room）JavaPOS 找零机技术栈。其设计源自一次真实迁移：把 1,600 多台支付终端从厂商锁定的服务迁到一个分层、可测试的运行时，该运行时连续交付五个月度版本且零 Sev-1 事故。本仓库保留了其中的架构与模式，同时剔除了厂商专有细节；`device-mybrand-cashchanger` 是一个可直接照搬的范例。

[![CI](https://github.com/anyingiit/Jpos-glue/actions/workflows/ci.yml/badge.svg)](https://github.com/anyingiit/Jpos-glue/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/anyingiit/Jpos-glue)](LICENSE)

[报告缺陷](https://github.com/anyingiit/Jpos-glue/issues/new?template=bug_report.yml) · [提出功能建议](https://github.com/anyingiit/Jpos-glue/issues/new?template=feature_request.yml) · [参与贡献](CONTRIBUTING.md) · [变更日志](CHANGELOG.md)

## 为什么有这个项目
- 用确定性分帧和明确的状态查询命令消除半双工串口通信中的歧义（ENQ/响应竞争），而不是靠时序上的运气。
- 隔离职责：设备状态机 ↔ 重试策略 ↔ 传输桥接 ↔ 编解码器 ↔ 控制面（JavaPOS）。
- 让逆向得到的协议可维护：加入自动化测试和可观测性钩子，即便缺少完整的厂商文档，重构也是安全的。
- 支持分阶段切换：新通信链路并行运行，逐步放量，并在任一环节都保留中止/回退的能力。

## 模块地图
```
control-core/               // 设备状态、重试/超时策略、service/control 骨架
comm-adapters/serial-bridge // 传输适配器（jSerialComm），带 ACK 门控与分帧读取
device-mybrand-cashchanger/ // 示例设备插件：codec + service + control + 桥接装配
```

## 内建的可靠性模式
- **确定性状态**：`DeviceStateMachine` 让设备生命周期保持显式（CLOSED/CLAIMED/ENABLED/BUSY/IDLE/ERROR）。
- **重试 + 超时 + 中止**：`RetryPolicy` 为每条命令包上有限次重试、超时以及设备专有的中止字节。
- **分帧 I/O**：`SerialBridge` 强制先 ACK 后正文，并用谓词判定读取终点（不会把残帧泄漏到下一次调用）。
- **编解码隔离**：设备专有的分帧/解析集中在 `MyCashChangerCodec`，使传输与业务逻辑彼此正交。
- **线程安全**：service 中的单线程 executor 加上同步的 control 方法，避免并发抢占同一条链路。

## 运行示例
前置条件：Java 17+ 和 Gradle wrapper。

构建全部模块：
```
./gradlew build
```

只构建示例设备：
```
./gradlew :device-mybrand-cashchanger:build
```

最小用法示意（把 `COM3` 换成你的回环/USB 串口，并换上真实的编解码器）：
```java
var control = new com.example.mybrand.MyCashChangerControl();
control.dispenseCash("1000,1");      // 走重试/超时策略的命令
var counts = control.readCashCounts();
control.smartDispense(5000);         // 设备专有扩展
```

## 把这套骨架移植到真实设备
1) 复制 `device-mybrand-cashchanger`，重命名包名，并把桥接指向你的端口/波特率。  
2) 在你的编解码器中实现 `encode`/`decode` 辅助方法以及报文结束谓词。  
3) 把设备操作映射到 `Command`（或新增更多），通过 `runWithPolicy` 串起来。  
4) 依据协议保证，为每条命令设置 `RetryPolicy` 的超时与中止字节。  
5) 用串口模拟器或回环夹具补上集成测试；抓取报文轨迹以锁定分帧行为。  
6) 上线前叠加可观测性（关联 ID、结构化日志、健康检查）。  

## 生产结果（本仓库的背景）
- 1,600 多台终端从黑盒服务迁移到这套分层运行时。
- 消除了 ENQ/响应歧义：用一条独立的状态查询命令取代 ENQ，把风险窗口压缩到结构上不可能发生。
- 自动化测试（JUnit/Gradle/Jenkins）达到约 79% 行覆盖率 / 70% 分支覆盖率（JaCoCo），让逆向得到的代码库也能放心重构。
- 连续五个月度版本，零 Sev-1 事故，零回滚。

## 待办事项
- 补上针对串口模拟器或硬件在环装置的集成测试。
- 提供结构化日志/指标钩子（时延、重试、中止、分帧错误）。
- 把构件发布到内部 Maven 仓库，并补充 JavaPOS service.xml 的装配说明。
- 扩展示例，加入 TCP 传输和第二种设备，以演示多租户部署。

## 参与贡献

欢迎贡献。[CONTRIBUTING.md](CONTRIBUTING.md) 说明了如何提交 Issue 或 Pull Request、如何配置构建，以及如何保持模块分层不被破坏；[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) 列出了对每一位参与者的行为要求。

请不要在公开的 Issue 或 Pull Request 中报告安全问题。[SECURITY.md](SECURITY.md) 说明了如何私下提交。

重要变更记录在 [CHANGELOG.md](CHANGELOG.md) 中。

## 许可证

本项目依据 MIT 许可证发布，详见 [LICENSE](LICENSE)。

`*/libs/` 下随仓库提供的 jar 包（`jSerialComm`、`javapos`）沿用各自上游的许可证。

## 联系方式

项目地址：[https://github.com/anyingiit/Jpos-glue](https://github.com/anyingiit/Jpos-glue)
