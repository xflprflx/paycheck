package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileProcessor {
    List<InvoiceDTO> returnInvoiceListFromFile(MultipartFile file) throws IOException;
    List<TransportDocumentDTO> returnTransportDocumentListFromFile(MultipartFile file) throws IOException;
}
