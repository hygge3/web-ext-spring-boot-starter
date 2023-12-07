#!/bin/bash
#
# ================================================ 说明 start ================================================
# 自动根据所在目录获取最新的可执行 jar、war
# 自动使用环境变量 JAVA_HOME 进行执行命令（可配置）
# 支持优雅下线（默认等待60秒，可配置，如果超过60秒则强制下线）
# 支持监听 SpringBoot 上线状态
# 支持添加自定义运行参数（如：JVM 参数、SpringBoot 参数）
# 支持常用功能：启动、停止、重启、查看状态、查看日志
# SpringBoot 项目支持查看应用端口信息
# ================================================ 说明 end   ================================================
#
# ================================================ 使用说明 start ================================================
# 1. 赋予执行权限: chmod +x boot.sh
# 2. 启动应用程序: sh boot.sh start
# 3. 停止应用程序: sh boot.sh stop
# 4. 查看应用程序: sh boot.sh status
# 5. 重启应用程序: sh boot.sh restart c # 加 c 删除历史日志文件
# 6. 查看应用日志: sh boot.sh logs
# ================================================ 使用说明 end   ================================================
#
# ================================================ 参数 start ================================================
# ------ Java 路径, 可以是 JAVA_HOME 也可以 Java 可执行文件路径, 默认使用环境变量的 JAVA_HOME
#JAVA="/usr/java/latest/bin/java"
#JAVA="/usr/java/latest/"
JAVA="${JAVA_HOME}"
# ------ 文件路径
DIR_PATH=$(
  cd $(dirname $0) || exit
  pwd
)
# ------ 服务文件名（默认使用目录下最新的.jar或者.war文件）
SERVER_FILE_NAME=$(ls -t ${DIR_PATH} | egrep '\.jar|\.war' | head -1)
# ------ Java 参数
JAVA_OPT="${JAVA_OPT} -server -Djava.security.egd=file:/dev/./urandom"
# 调整堆大小：通过 -Xms 和 -Xmx 参数来调整堆大小。合理的堆大小设置可以减少垃圾回收的频率，从而提高性能。
JAVA_OPT="${JAVA_OPT} -Xms128m -Xmx512m"
# 异步日志
JAVA_OPT="${JAVA_OPT} -Xlog:async"
# 启用编译优化：启用分层编译，这将在运行时进行更多的 JIT 编译，提高应用程序的性能
JAVA_OPT="${JAVA_OPT} -XX:+TieredCompilation"
# 启用多线程处理器：来启用非一致性内存访问 (NUMA) 支持，以提高多核系统上的性能。
JAVA_OPT="${JAVA_OPT} -XX:+UseNUMA"
# 当 JVM 发生 OOM 时，自动生成 DUMP 文件
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError"
# 生成DUMP文件的路径
JAVA_OPT="${JAVA_OPT} -XX:HeapDumpPath=./logs/dump/"
# GC日志配置
JAVA_OPT="${JAVA_OPT} -Xlog:safepoint=debug,classhisto*=debug,age*=info,gc*=info:file=./logs/gc-%t.log:time,level,tid,tags:filesize=50M"
# 开启ZGC
JAVA_OPT="${JAVA_OPT} -XX:+UseZGC"
#关闭主动GC周期，在主动回收模式下，ZGC 会在系统空闲时自动执行垃圾回收，以减少垃圾回收在应用程序忙碌时所造成的影响。如果未指定此参数（默认情况），ZGC 会在需要时（即堆内存不足以满足分配请求时）执行垃圾回收。
JAVA_OPT="${JAVA_OPT} -XX:-ZProactive"
# ------ Spring 参数
SPRING_OPT="${SPRING_OPT} --spring.profiles.active=test"
# ------ 控制台文件
#CONSOLE_FILE="/dev/null"
CONSOLE_FILE="${DIR_PATH}/logs/console.log"
# ------ 停止时等待超时的秒数
STOP_TIMEOUT=60
#
# ================================================ 参数 end   ================================================
#
# ================================================ 方法 start ================================================
#
# 服务PID
_SERVER_PID=0
# 服务端口
_SERVER_PORT=0
# 日志目录
_LOG_PATH="${DIR_PATH}/logs"
# 输入的第二个参数
_P2=$2
#
# =======================================
# 解析服务的 pid
# =======================================
resolve_server_pid() {
  _SERVER_PID=0
  if [ -f "${_LOG_PATH}/server.pid" ]; then
    # pid 文件存在则读取文件
    _SERVER_PID=$(cat "${_LOG_PATH}/server.pid")
    # 判断是否真的存在这个 pid
    if test $(ps -ef | awk '{print $2}' | grep -w ${_SERVER_PID} | wc -l) -eq 0; then
      # 没有找到文件记录的 pid, 删除这个文件
      rm -rf ${_LOG_PATH}/server.pid
      sleep 0.2
      _SERVER_PID=0
      # 重新解析服务的 pid
      #resolve_server_pid
    fi
  #else
  #    # pid文件不存在则尝试根据应用程序的名称获取pid
  #    if test $(pgrep -f ${SERVER_FILE_NAME} | wc -l) -gt 0; then
  #        # 说明应用程序正在跑, 使用第一个pid
  #        _SERVER_PID=$(pgrep -f ${SERVER_FILE_NAME} | head -1)
  #    else
  #        _SERVER_PID=0
  #    fi
  fi
}
#
# =======================================
# 解析服务的端口
# =======================================
retry_resolve_server_port() {
  _SERVER_PORT=0
  # 解析服务的 pid
  resolve_server_pid
  # 检测5分钟(300秒)
  for ((i = 0; i <= 300; i++)); do
    # 判断 pid 是否存在
    if test $(ps -ef | awk '{print $2}' | grep -w ${_SERVER_PID} | wc -l) -eq 0; then
      rm -rf ${_LOG_PATH}/server.pid
      _SERVER_PID=0
      break
    fi
    # 判断 pid 的 port 是否绑定
    if test $(netstat -tulnp | grep "${_SERVER_PID}/" | wc -l) -gt 0; then
      _SERVER_PORT=$(netstat -tulnp | grep "${_SERVER_PID}/" | head -1 | awk '{print $4}' | awk -F ":" '{print $NF}')
      break
    fi
    [[ $i%5 -eq 0 ]] && echo "观察服务器端口使用情况 ${i}s"
    sleep 1
  done
}
#
# =======================================
# 启动
# =======================================
start() {
  echo "---------- 启动 [start] ----------"
  # 解析服务的 pid
  resolve_server_pid
  if [ "${_SERVER_PID}" -ne 0 ]; then
    # 如果 _SERVER_PID != 0, 那就说明应用程序在运行,不进行启动
    echo "请不要尝试启动, ${SERVER_FILE_NAME} 已经在运行了! [ SERVER_PID = ${_SERVER_PID} ]"
    echo "---------- 启动 [1] ----------"
    return
  fi
  #
  echo "启动 ${SERVER_FILE_NAME} 中 ..."
  # 如果 JAVA 是一个目录,则加上/bin/java
  [ -d "${JAVA}" ] && JAVA="${JAVA}/bin/java"
  # 如果 JAVA 不是一个存在的文件, 则尝试使用默认的java
  [ ! -e "$JAVA" ] && JAVA=$(which java)
  # 最终 Java 不是一个存在的文件, 则清空
  [ ! -e "$JAVA" ] && unset JAVA
  if [ -z "${JAVA}" ]; then
    echo -e "\033[31m请在您的环境中设置JAVA_HOME变量，我们需要java（x64）！jdk17版本更好！\033[0m"
    exit 1
  fi
  # 判断是否需要删除日志文件
  if [[ "$_P2" == "c" ]]; then
    # 如果第二个参数是c, 删除日志文件
    echo -e "\033[33m删除 ${SERVER_FILE_NAME} 的日志文件(${DIR_PATH}/logs/*)\033[0m"
    rm -rf ${DIR_PATH}/logs/*
  fi
  # 检查日志目录
  test -d ${DIR_PATH}/logs/ || mkdir -p ${DIR_PATH}/logs/
  # 执行脚本
  # nohup java -Xmx128m -jar /home/app/server.jar --spring.profiles.active=prod >> /home/app/logs/console.log 2>&1 & echo $! > /home/app/logs/server.pid
  #
  # 运行的参数
  RUN_OPT="${JAVA_OPT} -jar"
  RUN_OPT="${RUN_OPT} ${DIR_PATH}/${SERVER_FILE_NAME}"
  [ -n "${SPRING_OPT}" ] && RUN_OPT="${RUN_OPT} ${SPRING_OPT}"
  #
  echo "$JAVA ${RUN_OPT}"
  echo "$JAVA ${RUN_OPT}" >${CONSOLE_FILE}
  nohup $JAVA ${RUN_OPT} >>${CONSOLE_FILE} 2>&1 &
  echo $! >${_LOG_PATH}/server.pid
  #
  echo "${SERVER_FILE_NAME} 正在运行"
  echo -e "你可以查看日志文件 \033[36m${CONSOLE_FILE}\033[0m 或者执行下面的命令"
  echo -e "\033[40;37mtail -f -n 300 ${CONSOLE_FILE}\033[0m"
  # 睡眠1秒
  sleep 1
  # 解析服务的 pid
  resolve_server_pid
  # 判断应用程序是否成功跑起来
  if [ "${_SERVER_PID}" -eq 0 ]; then
    echo -e "\033[31m${SERVER_FILE_NAME} 启动失败\033[0m"
  else
    if [ -n "${SPRING_OPT}" ]; then
      # 如果是 Spring 项目, 需要观察端口是否成功绑定
      retry_resolve_server_port
      #
      if [ "${_SERVER_PORT}" -eq 0 ]; then
        echo -e "\033[31m${SERVER_FILE_NAME} 启动失败\033[0m"
      else
        _SERVER_PORT_TEXT=$(netstat -tulnp | grep "${_SERVER_PID}/" | awk 'BEGIN{ORS=" "}{print $4}')
        echo -e "\033[32m${SERVER_FILE_NAME} 启动成功 [ SERVER_PID = ${_SERVER_PID}, SERVER_PORT = ${_SERVER_PORT_TEXT}]\033[0m"
      fi
    else
      echo -e "\033[32m${SERVER_FILE_NAME} 启动成功 [ SERVER_PID = ${_SERVER_PID}, \033[0m\033[33mSERVER_PORT = unknown \033[0m\033[32m]\033[0m"
    fi
  fi
  echo "---------- 启动 [end] ----------"
}
#
# =======================================
# 停止
# =======================================
stop() {
  echo "---------- 停止 [start] ----------"
  # 解析服务的 pid
  resolve_server_pid
  if [ "${_SERVER_PID}" -eq 0 ]; then
    # _SERVER_PID = 0, 那就说明应用程序没有在运行
    echo "${SERVER_FILE_NAME} 已停止"
    echo "---------- 停止 [end] ----------"
    return
  fi
  echo "停止 ${SERVER_FILE_NAME} 中..."
  # 进行优雅停机
  kill -15 ${_SERVER_PID}
  # 当前等待秒数
  _wait_seconds=0
  #
  while true; do
    if test $(ps -ef | awk '{print $2}' | grep -w ${_SERVER_PID} | wc -l) -eq 0; then
      echo "${SERVER_FILE_NAME} 已停止"
      break
    fi
    if [ "${_wait_seconds}" -ge "${STOP_TIMEOUT}" ]; then
      # 强制终止进程
      echo -e "\033[31m等待超时(${_wait_seconds}s), 强制关机 [kill -9 ${SERVER_FILE_NAME}].\033[0m"
      sudo kill -9 ${_SERVER_PID}
      break
    fi
    sleep 1
    let _wait_seconds++
    echo "请稍等 ${_wait_seconds}s"
  done
  if [ $? -eq 0 ]; then
    echo "${SERVER_FILE_NAME} 停止成功"
    rm -rf ${_LOG_PATH}/server.pid
  else
    echo "${SERVER_FILE_NAME} 停止失败"
  fi
  echo "---------- 停止  [end] ----------"
  sleep 1s
}
#
# =======================================
# 状态
# =======================================
status() {
  echo "---------- 状态 [start] ----------"
  # 解析服务的 pid
  resolve_server_pid
  if [ "${_SERVER_PID}" -eq 0 ]; then
    echo -e "\033[33m$SERVER_FILE_NAME 已停止\033[0m"
  else
    _SERVER_PORT_TEXT=$(netstat -tulnp | grep "${_SERVER_PID}/" | awk 'BEGIN{ORS=" "}{print $4}')
    [ -z "$_SERVER_PORT_TEXT" ] && _SERVER_PORT_TEXT="unknown "
    echo -e "\033[32m${SERVER_FILE_NAME} 运行中 [ SERVER_PID = ${_SERVER_PID}, SERVER_PORT = ${_SERVER_PORT_TEXT}]\033[0m"
    echo -e "你可以查看日志文件 \033[36m${CONSOLE_FILE}\033[0m 或者执行下面的命令"
    echo -e "\033[40;37mtail -f -n 300 ${CONSOLE_FILE}\033[0m"
  fi
  echo "---------- 状态 [end] ----------"
}
#
# =======================================
# 日志
# =======================================
logs() {
  tail -f -n 200 ${CONSOLE_FILE}
}
#
# ================================================ 方法 end   ================================================
#
case "$1" in
'start')
  start
  ;;
'stop')
  stop
  ;;
'restart')
  stop
  start
  ;;
'status')
  status
  ;;
'logs')
  logs
  ;;
*)
  echo "必须参数缺失 [start|stop|restart|status|logs] [c]?"
  exit 1
  ;;
esac
