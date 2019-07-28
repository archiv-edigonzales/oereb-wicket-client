package ch.so.agi.oereb.wicketclient.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;

@Service
public class ExtractService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Value("${app.webServiceUrl}")
    private String webServiceUrl;

    @Autowired
    Jaxb2Marshaller marshaller;

    public GetExtractByIdResponse getExtractByEgrid(String egrid) throws IOException {
        logger.info(egrid);

        // TODO: handle empty file / no extract returned
        File xmlFile = Files.createTempFile("data_extract_", ".xml").toFile();

        URL url = new URL(webServiceUrl + egrid);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        try (FileOutputStream xmlOutputStream = new FileOutputStream(xmlFile)) {
            xmlOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        logger.info("File downloaded: " + xmlFile.getAbsolutePath());

        StreamSource xmlSource = new StreamSource(xmlFile);

        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
//        logger.info(obj.getValue().getExtract().getValue().getExtractIdentifier());
//        logger.info(obj.getValue().getExtract().getValue().getRealEstate().getMunicipality());
        
        List<ThemeType> concernedThemes = obj.getValue().getExtract().getValue().getConcernedTheme();
//        for (ThemeType theme : concernedThemes) {
//            logger.info(theme.getCode());
//            logger.info(theme.getText().getText());
//            logger.info("-----");
//        }

        return obj;        
    }
}
