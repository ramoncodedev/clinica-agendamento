package com.agendamento.clinica.Nutricionista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutricionistaRepository extends JpaRepository<Nutricionista, Long> {

    boolean existsByCrn(String crn);
    boolean existsByEmail(String email);
}
