package com.finops.cloudcost.simulator;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloudMetricSimulator {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Random random = new Random();
    private static final String TOPIC = "cloud-metrics";

    // This background task runs automatically every 60 seconds on your laptop
    @Scheduled(fixedRate = 60000)
    public void produceMockMetrics() {
        String serverId = "i-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Intentionally create a highly "wasted" server 30% of the time to test our system
        boolean isWasted = random.nextInt(100) < 30; 
        
        int cpuUsage = isWasted ? random.nextInt(4) : random.nextInt(70) + 20; // 0-3% if wasted, 20-90% if normal
        int monthlyCost = isWasted ? random.nextInt(50000) + 30000 : random.nextInt(15000) + 5000; // Expensive if wasted

        // Construct a realistic JSON structure matching enterprise cloud formats
        String logPayload = String.format(
            "{\"serverId\":\"%s\", \"cpuUtilization\":%d, \"monthlyCostINR\":%d, \"environment\":\"production\"}",
            serverId, cpuUsage, monthlyCost
        );

        // 1. Print to your VS Code terminal console
        System.out.println("🚀 [SIMULATOR] Streaming Live Metric to Kafka -> " + logPayload);
        
        // 2. Stream it directly into your local running Docker Kafka channel
        try {
            kafkaTemplate.send(TOPIC, serverId, logPayload);
        } catch (Exception e) {
            System.err.println("❌ Failed to send metric to Kafka: " + e.getMessage());
        }
    }
}
