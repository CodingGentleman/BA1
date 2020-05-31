# Docker setup

After docker-compose up, a user has to be created:

Enter the container with:
```
docker exec -it OracleDB bash -c "source /home/oracle/.bashrc; sqlplus /nolog"
```

Once connected execute the following commands:

```sql
connect sys as sysdba;
```
The default password for the container is `Oradoc_db1`

```sql
alter session set "_ORACLE_SCRIPT"=true;
create user student identified by student;
GRANT CONNECT, RESOURCE, DBA TO student;
```
