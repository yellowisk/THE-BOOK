package br.edu.ifsp.thebook.external.persistence;

import br.edu.ifsp.thebook.domain.book.Book;
import br.edu.ifsp.thebook.domain.book.BookState;
import br.edu.ifsp.thebook.domain.reading.Reading;
import br.edu.ifsp.thebook.domain.reading.ReadingState;
import br.edu.ifsp.thebook.usecases.reading.gateway.ReadingDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ReadingDAOlmpl implements ReadingDAO {
    private JdbcTemplate jdbcTemplate;

    public ReadingDAOlmpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${queries.sql.reading-dao.insert.reading}")
    private String insertReadingQuery;

    @Value("${queries.sql.reading-dao.select.all}")
    private String selectAllReadingsQuery;

    @Value("${queries.sql.reading-dao.select.reading}")
    private String selectReadingQuery;

    @Value("${queries.sql.reading-dao.update.state}")
    private String updateReadingStateQuery;

    @Value("queries.sql.reading-da0.update.favorite")
    private String updateReadingFavoriteQuery;

    @Override
    public Reading findByUserIdAndBookId(UUID idUser, UUID idBook) {
        return jdbcTemplate.queryForObject(selectReadingQuery, this::mapperReadingFromRs,
                idUser, idBook);
    }

    @Override
    public Reading addNewReading(Reading reading) {
        UUID readingId = UUID.randomUUID();

        jdbcTemplate.update(insertReadingQuery, readingId, reading.getIdUser(), reading.getIdBook(),
                Timestamp.valueOf(LocalDateTime.now()), ReadingState.LENDO.name(), false, 0);

        return reading.createWithId(readingId);
    }

    @Override
    public List<Reading> findAll() {
        return jdbcTemplate.query(selectAllReadingsQuery, this::mapperReadingFromRs);
    }

    @Override
    public Reading updateFavorite(Reading reading) {
        jdbcTemplate.update(updateReadingFavoriteQuery, !reading.isFavorite(),
                reading.getIdUser(), reading.getIdBook());

        Reading readingDB = findByUserIdAndBookId(reading.getIdUser(), reading.getIdBook());
        return readingDB;
    }

    @Override
    public Reading updateState(Reading reading) {
        jdbcTemplate.update(updateReadingStateQuery, reading.getReadingState().name(),
                reading.getIdUser(), reading.getIdBook());

        Reading readingDB = findByUserIdAndBookId(reading.getIdUser(), reading.getIdBook());
        return readingDB;
    }

    public Reading mapperReadingFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID idUser = (UUID) rs.getObject("id_user");
        UUID idBook= (UUID) rs.getObject("id_book");
        Timestamp dateStart = rs.getTimestamp("date_start");
        ReadingState readingState = ReadingState.valueOf(rs.getString("status"));
        boolean isFavorite = rs.getBoolean("is_favorite");
        int rating = rs.getInt("rating");

        return Reading.createFull(id, idUser, idBook, dateStart, readingState, isFavorite, rating);
    }
}
