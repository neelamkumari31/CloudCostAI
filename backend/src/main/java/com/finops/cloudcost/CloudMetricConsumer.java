package com.finops.cloudcost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CloudMetricConsumer {

    @Autowired
    private WastedServerRepository serverRepository;

    @Autowired
    private CloudAiService aiService;

    @Autowired
    private EmailService emailService; // Injecting our fresh email delivery service

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "cloud-metrics", groupId = "finops-group")
    public void consumeMetrics(String incomingMessage) {
        try {
            // 1. Parse the incoming Kafka text log stream into json properties
            JsonNode jsonNode = objectMapper.readTree(incomingMessage);
            String serverId = jsonNode.get("serverId").asText();
            int cpu = jsonNode.get("cpuUtilization").asInt();
            int cost = jsonNode.get("monthlyCostINR").asInt();

            System.out.println("📥 [CONSUMER] Intercepted Log: " + serverId + " (CPU: " + cpu + "%)");

            // 2. Evaluation Rule: Trigger AI Architect optimization workflow for low utilization assets
            if (cpu < 5) {
                System.out.println("⚠️ [ALERT] Inefficiency Detected for " + serverId + "! Committing to Database...");

                // 3. Connect over secure internet call to fetch Gemini recommendation prompts
                String aiResponse = aiService.generateOptimizationFix(serverId, cpu, cost);
                
                // Fail-safe default string configurations if text parsing fails
                String summary = "Optimize underutilized server asset";
                String script = "# Manual verification required";

                // Substring splitting parameters to isolate Gemini's structural responses cleanly
                if (aiResponse.contains("SUMMARY:")) {
                    int summaryStart = aiResponse.indexOf("SUMMARY:") + 8;
                    int summaryEnd = aiResponse.contains("SCRIPT:") ? aiResponse.indexOf("SCRIPT:") : aiResponse.length();
                    summary = aiResponse.substring(summaryStart, summaryEnd).trim();
                }
                
                if (aiResponse.contains("SCRIPT:")) {
                    script = aiResponse.substring(aiResponse.indexOf("SCRIPT:") + 7).trim();
                }

                // 4. Generate a unique, cryptographically secure single-use access token
                String uniqueToken = UUID.randomUUID().toString();
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

                // 5. Build our updated master data entity layout block template
                WastedServer flaggedAsset = new WastedServer(serverId, cpu, cost, "PENDING_APPROVAL");
                flaggedAsset.setAiSummary(summary);
                flaggedAsset.setAiScript(script);
                flaggedAsset.setApprovalToken(uniqueToken);
                flaggedAsset.setTokenExpiry(expiryTime);

                // Commit tracking parameters right down to our running PostgreSQL container instance
                WastedServer saved = serverRepository.save(flaggedAsset);
                System.out.println("💾 [DATABASE] Saved entry tracking ID: " + saved.getId());

                // 6. Formulate the official REST framework web endpoint execution address link path
                String mobileApprovalLink = "http://localhost:8080/api/approve?token=" + uniqueToken;
                
                // 7. DISPATCH PROCESSOR: Fires a real interactive layout directly to your personal inbox space
                // !! REPLACE THE PLACEHOLDER EMAIL BELOW WITH YOUR REAL PERSONAL INBOX ADDRESS !!
                emailService.sendApprovalEmail("YOUR_PERSONAL_EMAIL_HERE@gmail.com", serverId, summary, mobileApprovalLink);
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing stream metrics: " + e.getMessage());
        }
    }
}
