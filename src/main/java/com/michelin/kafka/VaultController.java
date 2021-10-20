package com.michelin.kafka;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller("/vaults")
public class VaultController {
    @Inject
    public List<VaultConfig> vaultConfigs;

    @Get
    List<String> listVaults(){
        return vaultConfigs.stream()
                .map(VaultConfig::getName)
                .collect(Collectors.toList());
    }

    @Consumes(MediaType.TEXT_PLAIN)
    @Post(value = "/{vault}")
    String vaultPassword(String vault, @Body String password){
        VaultConfig vaultConfig = vaultConfigs.stream()
                .filter(v -> vault.equals(v.getName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Vault '%s' not found", vault)));

        return vaultConfig.encryptAndFormat(password);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Post(value = "/{vault}")
    String vaultPassword(String vault, @Valid @Body Password body){
        return vaultPassword(vault, body.password);
    }

    @Introspected
    static class Password {
        @NotBlank
        public String password;
    }

}
