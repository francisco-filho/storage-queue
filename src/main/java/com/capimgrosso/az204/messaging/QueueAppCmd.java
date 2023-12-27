package com.capimgrosso.az204.messaging;

import com.azure.core.http.rest.PagedIterable;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.QueueProperties;

import java.util.logging.Logger;

public class QueueAppCmd {
    private static final Logger LOG = Logger.getLogger(QueueAppCmd.class.getSimpleName());
    public static void main(String[] args) {
        var url = "https://az204cg001.queue.core.windows.net/taskqueue";
        var credential = new DefaultAzureCredentialBuilder().build();

        var queueClient = new QueueClientBuilder()
                .credential(credential)
                .endpoint(url)
                .buildClient();

        queueClient.createIfNotExists();

        queueClient.sendMessage("hello queue's");
        queueClient.sendMessage("I was here");

        LOG.info("--- message length ---");
        QueueProperties properties = queueClient.getProperties();
        LOG.info(""+properties.getApproximateMessagesCount());

        PagedIterable<QueueMessageItem> messages =  queueClient.receiveMessages(10);
        LOG.info("Receiving messages");
        for (var msg : messages) {
            LOG.info(msg.getBody().toString());
            queueClient.deleteMessage(msg.getMessageId(), msg.getPopReceipt());
        }
    }
}
