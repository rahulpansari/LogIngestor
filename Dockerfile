# Use a base image (e.g., official Cassandra image from Docker Hub)
FROM cassandra:latest

# Optionally, copy custom Cassandra configurations if needed
# COPY C:\Users\lucky\OneDrive\Documents\apache-cassandra-3.0.28-bin.tar\apache-cassandra-3.0.28\conf\cassandra.yaml /etc/cassandra/

# Expose ports (Cassandra default ports: 7000, 7001, 7199, 9042, 9160)
EXPOSE 7000 7001 7199 9042 9160

# Define environment variables if necessary (e.g., HEAP size, etc.)

# Start Cassandra when the container starts
CMD ["cassandra", "-f"]
