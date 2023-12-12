package ru.pyatkov.librarybackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Person")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name")
    @NotEmpty(message = "ФИО не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    private String fullName;

    @Column(name = "year_of_birth")
    @Min(value = 1900, message = "Год рождения должен быть больше, чем 1900")
    private int yearOfBirth;

    @ToString.Exclude
    @OneToMany(mappedBy = "owner")
    private List<Book> books;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

}
