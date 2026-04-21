default:
    @echo "just start-all / start-redis / start-db / stop"

build:
    cd RuoYi-Vue-springboot3 && mvn clean compile -DskipTests
    cd RuoYi-Vue3-v3.8.8 && bun install

start-redis:
    mkdir -p .local/redis
    redis-server --dir $PWD/.local/redis --pidfile $PWD/.local/redis/redis.pid --daemonize yes

start-db:
    mkdir -p .local/mysql
    mariadbd --datadir $PWD/.local/mysql --pid-file $PWD/.local/mysql/mysql.pid --socket $PWD/.local/mysql/mysql.sock --port 3306 &

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
