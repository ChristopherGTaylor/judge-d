package com.hltech.judged.server.interfaces.rest.interrelationship;

import com.hltech.judged.server.domain.environment.Service;
import com.hltech.judged.server.interfaces.rest.contracts.ServiceContractsDto;
import com.hltech.judged.server.domain.contracts.ServiceContracts;
import com.hltech.judged.server.domain.contracts.ServiceContractsRepository;
import com.hltech.judged.server.domain.environment.EnvironmentRepository;
import com.hltech.judged.server.domain.ServiceVersion;
import com.hltech.judged.server.interfaces.rest.contracts.ContractsMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class InterrelationshipController {

    private final EnvironmentRepository environmentRepository;
    private final ServiceContractsRepository serviceContractsRepository;
    private final ContractsMapper contractsMapper;

    @CrossOrigin
    @GetMapping(value = "/interrelationship/{environment}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get interrelationship between services in given environment", nickname = "Validate against environment")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = InterrelationshipDto.class),
        @ApiResponse(code = 500, message = "Failure")
    })
    public InterrelationshipDto getInterrelationship (@PathVariable("environment") String env) {

        Set<ServiceContractsDto> serviceContractsSet =  environmentRepository.get(env).getAllServices().stream()
            .map(this::getServiceContracts)
            .map(contractsMapper::toDto)
            .collect(Collectors.toSet());

        return new InterrelationshipDto(env, serviceContractsSet);
    }

    private ServiceContracts getServiceContracts(Service service) {
        return serviceContractsRepository.findOne(new ServiceVersion(service.getName(), service.getVersion()))
            .orElseGet(() ->
                new ServiceContracts(
                    new ServiceVersion(service.getName(), service.getVersion()),
                    new ArrayList<>(),
                    new ArrayList<>()));
    }
}
