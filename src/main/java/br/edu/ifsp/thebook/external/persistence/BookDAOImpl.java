package br.edu.ifsp.thebook.external.persistence;

import br.edu.ifsp.thebook.domain.book.Book;
import br.edu.ifsp.thebook.domain.book.BookState;
import br.edu.ifsp.thebook.usecases.book.gateway.BookDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class BookDAOImpl implements BookDAO {

    private JdbcTemplate jdbcTemplate;

    public BookDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${queries.sql.book-dao.insert.book}")
    private String insertBookQuery;

    @Value("${queries.sql.book-dao.select.all}")
    private String selectAllBooksQuery;

    @Override
    public Book addNewBook(Book book) {
        UUID bookId = UUID.randomUUID();

        jdbcTemplate.update(insertBookQuery, bookId, book.getPages(), book.getTitle(),
                book.getGender(), book.getAuthor(), book.getClassification(), book.getSummary(),
                book.getDataAdd(), book.getIdUserAdd(), book.getBookState().name(), book.getAverage());

        return book.createWithId(bookId);
    }

    @Override
    public List<Book> findAll() {
        return jdbcTemplate.query(selectAllBooksQuery, this::mapperBookFromRs);
    }

    public Book mapperBookFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        int pages = rs.getInt("pages");
        String title = rs.getString("title");
        String gender = rs.getString("gender");
        String author = rs.getString("author");
        int classification = rs.getInt("classification");
        String summary = rs.getString("summary");
        Timestamp dataAdd = rs.getTimestamp("date_add");
        UUID idUserAdd = (UUID) rs.getObject("id_user_add");
        BookState bookState = BookState.valueOf(rs.getString("status"));
        int average = rs.getInt("average");

        return Book.createFull(id, pages, title, gender, author, classification, summary, dataAdd, idUserAdd, bookState, average);
    }

}
