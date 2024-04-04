package com.xflprflx.paycheck.controllers;

import com.aspose.pdf.*;
import com.xflprflx.paycheck.domain.Payment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @PostMapping
    public String tableFromPdfToPaymentObject(@RequestParam("file")MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Carregar documento PDF de origem
            com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(inputStream);

            // Variável para armazenar a data extraída do PDF
            String date = extrairDataDoPDF(pdfDocument);
            var dateSplit = date.split("/");
            var day = dateSplit[0];
            var month = dateSplit[1];
            var year = dateSplit[2];
            date = year + "-" + month + "-" + day + "T00:00:00.000";
            System.out.println(date);

            // Criar um absorvente de mesa
            com.aspose.pdf.TableAbsorber absorber = new com.aspose.pdf.TableAbsorber();

            // Digitalizar páginas
            for (com.aspose.pdf.Page page : pdfDocument.getPages()) {
                // Visualizar página
                absorber.visit(page);

                // Iterar pelas tabelas
                for (com.aspose.pdf.AbsorbedTable table : absorber.getTableList()) {
                    boolean isHeaderRow = true; // Flag para identificar o cabeçalho da tabela
                    System.out.println("Table");

                    // Iterar através da lista de linhas
                    for (com.aspose.pdf.AbsorbedRow row : table.getRowList()) {
                        if (isHeaderRow) {
                            // Pular a primeira linha se for o cabeçalho
                            isHeaderRow = false;
                            continue;
                        }

                        // Instanciar um objeto Payment para a linha atual
                        Payment payment = createPaymentFromRow(row);
                        payment.setPaymentDate(date);

                        // Agora você pode fazer o que quiser com o objeto Payment,
                        // como salvá-lo em um banco de dados ou processá-lo de outra forma
                        System.out.println(payment.toString()); // Exemplo de como você pode usá-lo
                    }
                }
            }
        } catch (IOException e) {
            return "Erro";
        }
        return "Segura";
    }

    private Payment createPaymentFromRow(com.aspose.pdf.AbsorbedRow row) {
        Payment payment = new Payment();

        // Obtendo os fragmentos de texto da linha
        java.util.List<String> textFragments = new java.util.ArrayList<>();
        boolean cellIsEmpty = true;
        for (com.aspose.pdf.AbsorbedCell cell : row.getCellList()) {
            if(cell.getTextFragments().size() > 0) {
                for (com.aspose.pdf.TextFragment fragment : cell.getTextFragments()) {
                    StringBuilder sb = new StringBuilder();

                    for (com.aspose.pdf.TextSegment seg : fragment.getSegments()) {
                        sb.append(seg.getText());
                    }
                    textFragments.add(sb.toString());
                }
            } else {
                textFragments.add("-");
            }

        }
        System.out.println(textFragments.size());
        // Preenchendo os campos do objeto Payment com base nos fragmentos de texto
        payment.setNumber(textFragments.get(0).split("-")[0]);
        payment.setSerie(textFragments.get(0).split("-")[1]);
        payment.setInvoiceSG(textFragments.get(1));
        payment.setMyInvoice(textFragments.get(2));
        payment.setDocCompensation(textFragments.get(3));
        payment.setAmount(Double.valueOf(textFragments.get(4).replace(",",".")));
        payment.setText(textFragments.get(5)); // Se houver mais campos, ajuste aqui conforme necessário


        return payment;
    }


    private String extrairDataDoPDF(com.aspose.pdf.Document pdfDocument) {
        // Criar um absorvente de fragmento de texto para extrair o texto do PDF
        TextFragmentAbsorber absorber = new TextFragmentAbsorber();

        // Iterar pelas páginas do PDF para extrair o texto
        for (Page page : pdfDocument.getPages()) {
            page.accept(absorber);
        }

        // Obter a coleção de fragmentos de texto extraídos
        TextFragmentCollection textFragmentCollection = absorber.getTextFragments();
        TextFragment tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);
        tf = textFragmentCollection.get_Item(1);
        textFragmentCollection.remove(tf);

        // Definir o padrão de expressão regular para encontrar a data no formato dd/MM/yyyy
        Pattern pattern = Pattern.compile("\\b(\\d{2})/(\\d{2})/(\\d{4})\\b");

        // Iterar pelos fragmentos de texto para encontrar a data
        for (TextFragment textFragment : textFragmentCollection) {
            String fragmentText = textFragment.getText();
            Matcher matcher = pattern.matcher(fragmentText);
            if (matcher.find()) {
                // Extrair os grupos correspondentes à data
                String extractedDate = matcher.group(1) + "/" + matcher.group(2) + "/" + matcher.group(3);
                System.out.println("Data extraída: " + extractedDate);
                return extractedDate;
            }
        }

        // Se a data não for encontrada, retornar null
        System.out.println("Data não encontrada.");
        return null;
    }

}


