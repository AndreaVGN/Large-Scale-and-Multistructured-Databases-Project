package com.example.WanderHub.demo.DTO;

import java.util.List;
import com.example.WanderHub.demo.model.Book;  // Assumendo che Review sia un modello esistente
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class BookDTO {

    private List<Book> books;  // Lista di recensioni

}



