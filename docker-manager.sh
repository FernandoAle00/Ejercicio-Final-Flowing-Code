#!/bin/bash

# Script de gestión para Docker PostgreSQL
# Uso: ./docker-manager.sh [comando]

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

function print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

function print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

function print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

function print_error() {
    echo -e "${RED}❌ $1${NC}"
}

function start_db() {
    print_header "Iniciando PostgreSQL"
    docker-compose up -d postgres
    sleep 3
    docker-compose ps postgres
    print_success "PostgreSQL iniciado en localhost:5432"
    echo ""
    echo "Credenciales:"
    echo "  Base de datos: sistema_academico"
    echo "  Usuario: admin"
    echo "  Contraseña: admin123"
}

function stop_db() {
    print_header "Deteniendo PostgreSQL"
    docker-compose stop postgres
    print_success "PostgreSQL detenido"
}

function restart_db() {
    print_header "Reiniciando PostgreSQL"
    docker-compose restart postgres
    print_success "PostgreSQL reiniciado"
}

function logs_db() {
    print_header "Logs de PostgreSQL"
    docker-compose logs -f postgres
}

function connect_db() {
    print_header "Conectando a PostgreSQL"
    docker exec -it sistema-academico-db psql -U admin -d sistema_academico
}

function status_db() {
    print_header "Estado de PostgreSQL"
    docker-compose ps
}

function clean_db() {
    print_warning "Esto eliminará TODOS los datos de la base de datos"
    read -p "¿Estás seguro? (y/N): " confirm
    if [[ $confirm == [yY] ]]; then
        print_header "Eliminando PostgreSQL y datos"
        docker-compose down -v
        print_success "Base de datos eliminada"
    else
        print_warning "Operación cancelada"
    fi
}

function backup_db() {
    print_header "Creando backup de la base de datos"
    BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
    docker exec -t sistema-academico-db pg_dump -U admin sistema_academico > "$BACKUP_FILE"
    print_success "Backup creado: $BACKUP_FILE"
}

function restore_db() {
    if [ -z "$1" ]; then
        print_error "Uso: ./docker-manager.sh restore <archivo.sql>"
        exit 1
    fi
    
    if [ ! -f "$1" ]; then
        print_error "Archivo no encontrado: $1"
        exit 1
    fi
    
    print_header "Restaurando backup: $1"
    docker exec -i sistema-academico-db psql -U admin sistema_academico < "$1"
    print_success "Backup restaurado"
}

function run_app() {
    print_header "Ejecutando aplicación Spring Boot"
    echo "Usando perfil: DEV (crea tablas automáticamente)"
    echo ""
    SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
}

function run_app_prod() {
    print_header "Ejecutando aplicación Spring Boot (PRODUCCIÓN)"
    echo "Usando perfil: PROD (actualiza tablas sin borrar)"
    echo ""
    SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
}

function build_app() {
    print_header "Construyendo aplicación con Docker"
    docker-compose up -d --build
    print_success "Aplicación construida y ejecutándose"
    echo ""
    echo "Accede a: http://localhost:8080"
}

function show_tables() {
    print_header "Tablas en la base de datos"
    docker exec -it sistema-academico-db psql -U admin -d sistema_academico -c "\dt"
}

function show_users() {
    print_header "Usuarios en la base de datos"
    docker exec -it sistema-academico-db psql -U admin -d sistema_academico -c "SELECT id, username, role FROM users;"
}

function show_help() {
    cat << EOF
${BLUE}Sistema Académico - Gestor de Docker PostgreSQL${NC}

${GREEN}Comandos disponibles:${NC}

  ${YELLOW}Base de Datos:${NC}
    start       Iniciar PostgreSQL
    stop        Detener PostgreSQL
    restart     Reiniciar PostgreSQL
    status      Ver estado de PostgreSQL
    logs        Ver logs de PostgreSQL
    connect     Conectar a PostgreSQL (psql)
    clean       Eliminar PostgreSQL y TODOS los datos ⚠️
    
  ${YELLOW}Datos:${NC}
    tables      Mostrar tablas
    users       Mostrar usuarios
    backup      Crear backup de la base de datos
    restore     Restaurar backup (uso: restore <archivo.sql>)
    
  ${YELLOW}Aplicación:${NC}
    run         Ejecutar aplicación localmente (perfil DEV - crea tablas)
    run-prod    Ejecutar aplicación localmente (perfil PROD - actualiza tablas)
    build       Construir y ejecutar con Docker
    
  ${YELLOW}Ayuda:${NC}
    help        Mostrar esta ayuda

${BLUE}Ejemplos:${NC}
    ./docker-manager.sh start
    ./docker-manager.sh connect
    ./docker-manager.sh backup
    ./docker-manager.sh restore backup_20250106_143022.sql
    ./docker-manager.sh users

EOF
}

# Main
case "$1" in
    start)
        start_db
        ;;
    stop)
        stop_db
        ;;
    restart)
        restart_db
        ;;
    logs)
        logs_db
        ;;
    connect)
        connect_db
        ;;
    status)
        status_db
        ;;
    clean)
        clean_db
        ;;
    backup)
        backup_db
        ;;
    restore)
        restore_db "$2"
        ;;
    run)
        run_app
        ;;
    run-prod)
        run_app_prod
        ;;
    build)
        build_app
        ;;
    tables)
        show_tables
        ;;
    users)
        show_users
        ;;
    help|--help|-h|"")
        show_help
        ;;
    *)
        print_error "Comando desconocido: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
