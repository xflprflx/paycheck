package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Parameters;
import com.xflprflx.paycheck.repositories.ParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ParametersService {

    @Autowired
    private ParametersRepository parametersRepository;

    @Transactional(readOnly = true)
    public Parameters getParams() {
        Optional<Parameters> optionalParameters = parametersRepository.findById(1);
        if (optionalParameters.isPresent()) {
            return optionalParameters.get();
        }
        return null;
    }

    @Transactional
    public Parameters  updateParams(Parameters parametersdto) {
        Parameters parameters;
        Optional<Parameters> optionalParameters = parametersRepository.findById(parametersdto.getId());
        if (optionalParameters.isPresent()) {
            parameters = optionalParameters.get();
        } else {
            parameters = new Parameters();
        }
        parameters.setPaymentTerms(parametersdto.getPaymentTerms());
        parameters = parametersRepository.save(parameters);
        return parameters;
    }
}
