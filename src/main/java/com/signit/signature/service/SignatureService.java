package com.signit.signature.service;

import com.emdha.esign.*;
import com.signit.signature.controller.dto.ResponseBodyDto;
import com.signit.signature.controller.dto.SignatureDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;

import esign.text.pdf.codec.Base64;

import javax.net.ssl.*;


@Service
public class SignatureService {

    private final Logger logger = LoggerFactory.getLogger(SignatureService.class);

    @Value("${signit.licence.pFXPassword}")
    private String pFXPassword;

    public ResponseBodyDto sign(SignatureDto inputData) {
        try {

            String tempFolderPath = "";
            // String tempFolderPath = "Temp";

            //In output folder path, signed PDFs are written.
            String outputFolder = "Output";

            //Licence File
            String licenceFilePath = "UAT-PRVT-SIGNIT02.lic";//replace with your license file

            //SIP Certificate
            String pFXFilePath = "ClientSDKDEV.pfx";
            String pFXPassword = "emdha";
            String pFXAlias = "fin";

            //SIP ID
            String SIPID = "UAT-PRVT-SIGNIT02"; //name of the license file is your sip

            //eSign URL
            String eSignURL = "https://esign-dev.emdha.sa/eSign/SignDoc";
            //KYC ID
            //TODO:remove it
            String kycId = "2047111111";

            int estimatedContent = 40000; //this value shoud be read either from DB or from the property file. 40000 is the default value.

            //Step-1.Convert PDF to Base64 encoded string
            String pdfPath = "Test.pdf";
            String docBase64 = docBase64(pdfPath);

            String appearanceBackgroundImage = "";

            //Step-2. Construct eSignEmdha object using licenceFile, PFXFile, PFXFile credentials and SIPID. (PFX file is also called certificate file.)
            //These files are available in the integration kit shared with you.
            //eSignEmdha eSign = new eSignEmdha(licenceFilePath, //pFXFilePath, pFXPassword, pFXAlias, SIPID); // by default signature contentEstimated is 40000
            eSignEmdha eSign = new eSignEmdha(licenceFilePath, pFXFilePath, pFXPassword, pFXAlias, SIPID, estimatedContent);

            //Step-3. Construct EmdhaSignerInfo object, the value to the parameters of this constructor is supposed to be obtained from Trusted KYC source with 2 factor authentication.
            EmdhaSignerInfo signerInfo = new EmdhaSignerInfo(inputData.getKycData().getOrgName(),
                String.valueOf(inputData.getKycData().getKycId()), inputData.getKycData().getEnglishName(),
                inputData.getKycData().getArabicName(), "0536014413",
                inputData.getKycData().getEmail(), inputData.getKycData().getCity(), inputData.getKycData().getRegion()
                , inputData.getKycData().getCountry());

            String signerInfox64 = signerInfo.getSignerInfoXMLBase64();

            //Step-4. Create EmdhaInput object.

            //for adding custom image/logo to the signature appearance.
            //appearanceBackgroundImage is the image of your organization logo. Please provide base64 of the logo.
            //Please choose eSignEmdha.SignatureAppearanceType.CUSTOM_LOGO in EmdhaInput constructor.
            //If eSignEmdha.SignatureAppearanceType.EMDHA_LOGO chosen then emdha logo appears. //default
            //If eSignEmdha.SignatureAppearanceType.NO_IMAGE chosen then no logo appears
            //appearanceBackgroundImage = docBase64("your organization logo.png"); //uncomment to use this


            //EmdhaInput, some other constructor samples
            //EmdhaInput input2 = new EmdhaInput(docBase64, "", true, "1-94,575,244,650;1-75,695,225,770",
            //       eSignEmdha.AppearanceRunDirection.RUN_DIRECTION_LTR, eSignEmdha.SignatureAppearanceType.EMDHA_LOGO, appearanceBackgroundImage, "Custom content سورابه");


            //invisible signature
            EmdhaInput input2 = new EmdhaInput(docBase64, "", true, "1-0,0,0,0",
                eSignEmdha.AppearanceRunDirection.RUN_DIRECTION_LTR, eSignEmdha.SignatureAppearanceType.NO_IMAGE, appearanceBackgroundImage, "");


            //EmdhaInput input3 = new EmdhaInput(docBase64, "Location", "Test", "SignedBy", true, eSignEmdha.Coordinates.BottomRight, "1,2",
            //eSignEmdha.AppearanceRunDirection.RUN_DIRECTION_LTR, eSignEmdha.SignatureAppearanceType.NO_IMAGE, "appearanceBackgroundImage", "");

            //Preapre a list of documents to be signed. Maximum 10 documents can be signed in a single transaction.
            ArrayList<EmdhaInput> inputs = new ArrayList<>();
            inputs.add(input2);

            //Step-5. Generate request XML to post it to EMDHA.
            //second parameter here is transaction ID. Transaction ID should be unique for each transaction. If passed empty SDK generates a transaction ID.
            EmdhaServiceReturn serviceReturn = eSign.generateRequestXml(inputs, "", tempFolderPath,
                signerInfox64, kycId, true, true, eSignEmdha.KYCIdProvider.SELF_NID);

//            logger.info(serviceReturn.getErrorMessage());
//            logger.info(serviceReturn.getStatus());
            if (serviceReturn.getStatus() != 1) {
            } else {
                //URL encode the generated request XML.
                String URLEncodedsignedRequestXML = URLEncoder.encode(serviceReturn.getRequestXML(), "UTF-8");

                //Step-6. Post request XML to emdha CA and get response XML for signing completion
                String response = postXML(eSignURL, URLEncodedsignedRequestXML);

                //Step-7. call getSignedDocuments method
                EmdhaServiceReturn eSignServiceReturn = eSign.getSignedDocuments(response, serviceReturn.getReturnValues()); //tempFilePath not required. Please make this path empty or remove it.

                //EmdhaServiceReturn eSignServiceReturn = eSign.getSignedDocuments(response, tempFolderPath); //temp file path is required. ".Sig" file is created for the transaction and written it to the tempFilePath.
                String pdfToBase64 = null;
                if (eSignServiceReturn.getStatus() == 1) {
                    int i = 0;

                    //call getReturnValues method to get signed PDFs
                    ArrayList<ReturnDocument> docs = eSignServiceReturn.getReturnValues();
                    for (ReturnDocument doc : docs) {
                        pdfToBase64 = doc.getSignedDocument();
                        byte[] signedBytes = Base64.decode(pdfToBase64);
                        String pdfOUT = "";

                        //Write signed PDF to output folder path
                        pdfOUT = outputFolder + File.separator + "Signed_PDF_" + i + ".pdf";
                        try (FileOutputStream fos = new FileOutputStream(pdfOUT)) {
                            fos.write(signedBytes);
                        } catch (Exception e) {
                            logger.error(e.toString());
                        }
                        i++;

                    }
                }
                return ResponseBodyDto.builder()
                    .signedDoc(pdfToBase64)
                    .status(eSignServiceReturn.getStatus())
                    .errorMessage(eSignServiceReturn.getErrorMessage())
                    .errorCode(eSignServiceReturn.getErrorCode())
                    .build();
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        return null;
    }


    private static String postXML(String eSignURL, String requestXML) throws Exception {
        URL url = new URL(eSignURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
        sslcontext.init(new KeyManager[0], new TrustManager[]{new DummyTrustManager()}, new SecureRandom());
        SSLSocketFactory factory = sslcontext.getSocketFactory();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(requestXML.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setSSLSocketFactory(factory);
        connection.setHostnameVerifier(new DummyHostnameVerifier());

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(requestXML);
            wr.flush();
        }
        InputStream is = connection.getInputStream();
        StringBuilder response;
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
            String line = null;
            response = new StringBuilder("");
            while ((line = rd.readLine()) != null) {
                response.append(line).append("\r");
            }
        }
        return response.toString();
    }

    private static String docBase64(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        byte[] samplePDF = new byte[(int) file.length()];
        inputStream.read(samplePDF);
        inputStream.close();
        return Base64.encodeBytes(samplePDF);
    }
}
