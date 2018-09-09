package dev.hltech.dredd.interfaces.rest.validation;

import com.google.common.collect.Ordering;
import dev.hltech.dredd.domain.validation.EnvironmentValidatorResult;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

public class Converters {

    public static List<ContractValidationReportDto> toDtos(List<EnvironmentValidatorResult> validationResults, String serviceName, String serviceVersion) {
        Map<ConsumerAndProviderDto, ContractValidationReportDto> result = newHashMap();
        validationResults
            .stream()
            .forEach(environmentValidatorResult -> {
                environmentValidatorResult.getCapabilitiesValidationResults()
                    .stream()
                    .forEach(cvr -> {
                        ConsumerAndProviderDto key = new ConsumerAndProviderDto(cvr.getConsumerName(), cvr.getConsumerVersion(), serviceName, serviceVersion);
                        if (!result.containsKey(key)) {
                            result.put(
                                key,
                                new ContractValidationReportDto(key)
                            );
                        }
                        result.get(key).addInteractions(
                            cvr.getInteractionValidationResults()
                                .stream()
                                .map(ivr -> new InteractionValidationReportDto(
                                    environmentValidatorResult.getCommunicationInterface(),
                                    ivr.getName(),
                                    ivr.getStatus(),
                                    ivr.getErrors()
                                ))
                                .collect(Collectors.toList())
                        );
                    });
                environmentValidatorResult.getExpectationValidationResults()
                    .stream()
                    .forEach(evr -> {
                        ConsumerAndProviderDto key = new ConsumerAndProviderDto(serviceName, serviceVersion, evr.getProviderName(), evr.getProviderVersion());
                        if (!result.containsKey(key)) {
                            result.put(
                                key,
                                new ContractValidationReportDto(key)
                            );
                        }
                        result.get(key).addInteractions(
                            evr.getInteractionValidationResults()
                                .stream()
                                .map(ivr -> new InteractionValidationReportDto(
                                    environmentValidatorResult.getCommunicationInterface(),
                                    ivr.getName(),
                                    ivr.getStatus(),
                                    ivr.getErrors()
                                ))
                                .collect(Collectors.toList())
                        );
                    });
            });

        return Ordering
            .from((Comparator<ContractValidationReportDto>) (o1, o2) -> o1.getConsumerAndProvider().toString().compareTo(o2.getConsumerAndProvider().toString()))
            .sortedCopy(result.values());
    }
}