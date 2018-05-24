FROM ilju3280/hospital:v2 
EXPOSE 5000 
ENTRYPOINT ["java", "-jar", "hospital.jar"]
