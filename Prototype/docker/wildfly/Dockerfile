FROM jboss/wildfly
RUN /opt/jboss/wildfly/bin/add-user.sh admin test --silent
COPY ./module/ /opt/jboss/wildfly/modules/system/layers/base/com/oracle/main/
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
