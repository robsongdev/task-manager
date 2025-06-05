package com.robson.task_manager.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank; // Importa a anotação para validação de string não vazia ou nula
import jakarta.validation.constraints.Size;    // Importa a anotação para validação de tamanho de string
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Classe de Modelo para a entidade Task.
 * Representa uma tarefa no sistema.
 * Adicionadas validações para garantir a integridade dos dados na entrada.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título da tarefa não pode estar em branco")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
    private String title;

    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres")
    private String description;

    private boolean completed;
}