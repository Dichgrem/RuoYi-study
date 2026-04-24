default:
    @echo "just start-all / start-redis / start-db / stop"

build:
    cd RuoYi-Vue-springboot3 && mvn clean compile -DskipTests
    cd RuoYi-Vue3-v3.8.8 && bun install

start-redis:
    mkdir -p .local/redis
    redis-server --dir $PWD/.local/redis --pidfile $PWD/.local/redis/redis.pid --daemonize yes

start-db:
    #!/usr/bin/env bash
    set -e
    mkdir -p .local/mysql
    # Check if DB is already running
    if mariadb-admin --socket="$PWD/.local/mysql/mysql.sock" -u root -p123456 ping >/dev/null 2>&1; then
        echo "MariaDB is already running."
        exit 0
    fi
    # Clean up any dead locks or sockets
    if [ -f .local/mysql/mysql.pid ]; then
        pid=$(cat .local/mysql/mysql.pid)
        if kill -0 "$pid" 2>/dev/null; then
            echo "Killing stuck MariaDB process $pid..."
            kill -9 "$pid"
            sleep 1
        fi
    fi
    rm -f .local/mysql/mysql.sock .local/mysql/mysql.pid .local/mysql/aria_log_control
    NEEDS_INIT=0
    if [ ! -d .local/mysql/mysql ]; then
        NEEDS_INIT=1
        echo "Initializing new MariaDB database..."
        mysql_install_db --datadir="$PWD/.local/mysql" --auth-root-authentication-method=normal >/dev/null
    fi
    echo "Starting MariaDB server..."
    mariadbd --datadir="$PWD/.local/mysql" --pid-file="$PWD/.local/mysql/mysql.pid" --socket="$PWD/.local/mysql/mysql.sock" --port=3306 >.local/mysql/mariadb.log 2>&1 &
    echo "Waiting for MariaDB to be ready..."
    if [ $NEEDS_INIT -eq 1 ]; then
        while ! mariadb-admin --socket="$PWD/.local/mysql/mysql.sock" -u root ping >/dev/null 2>&1; do sleep 0.5; done
        echo "Setting root password and importing SQL..."
        mariadb -u root --socket="$PWD/.local/mysql/mysql.sock" -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';"
        mariadb -u root -p123456 --socket="$PWD/.local/mysql/mysql.sock" -e "CREATE DATABASE IF NOT EXISTS \`ry-vue\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
        mariadb -u root -p123456 --socket="$PWD/.local/mysql/mysql.sock" -D "ry-vue" -e "source $PWD/RuoYi-Vue-springboot3/sql/ry_20260330.sql"
        mariadb -u root -p123456 --socket="$PWD/.local/mysql/mysql.sock" -D "ry-vue" -e "source $PWD/RuoYi-Vue-springboot3/sql/quartz.sql"
    else
        while ! mariadb-admin --socket="$PWD/.local/mysql/mysql.sock" -u root -p123456 ping >/dev/null 2>&1; do sleep 0.5; done
    fi
    echo "MariaDB is ready!"

start-backend:
    cd RuoYi-Vue-springboot3 && mvn -pl ruoyi-admin spring-boot:run &

start-frontend:
    cd RuoYi-Vue3-v3.8.8 && (test -d node_modules || bun install) && bun run dev &

start-all:
    just start-redis
    just start-db
    just start-backend
    just start-frontend

stop:
    -mariadb-admin -u root -p123456 shutdown 2>/dev/null || true
    -redis-cli shutdown 2>/dev/null || true
    -pkill -f "spring-boot:run" 2>/dev/null || true
    -pkill -f "vite" 2>/dev/null || true
