package com.stackroute.listener;

import com.stackroute.domain.PdfDocument;
import com.stackroute.exception.ParagraphNotFoundException;
import com.stackroute.service.ParagraphService;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumer {

    private ParagraphService paragraphService;
    private KafkaProducer kafkaProducer;

    @Autowired
    public KafkaConsumer(ParagraphService paragraphService,KafkaProducer kafkaProducer) {
        this.kafkaProducer=kafkaProducer;
        this.paragraphService = paragraphService;
    }

    @KafkaListener(topics = "FileText", groupId = "group_id")
    public void consume(String message) throws ParagraphNotFoundException {

        JSONObject object=(JSONObject) JSONValue.parse(message);
        PdfDocument pdfDocument=new PdfDocument(object.get("documentId").toString(),object.get("documentText").toString(),
                (JSONObject) object.get("documentMetaData"));
        System.out.println(message);

        List<JSONObject> paragraphList=paragraphService.getParagraphObject(pdfDocument.getDocumentId(),pdfDocument.getDocumentText());

        kafkaProducer.postservice(paragraphList);

        System.out.println("text\n"+pdfDocument.getDocumentText());
        System.out.println("Id\n"+pdfDocument.getDocumentId());
        System.out.println("metadata\n"+pdfDocument.getDocumentMetaData());
    }
}

