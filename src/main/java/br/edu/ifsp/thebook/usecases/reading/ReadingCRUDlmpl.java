package br.edu.ifsp.thebook.usecases.reading;

import br.edu.ifsp.thebook.domain.reading.Reading;
import br.edu.ifsp.thebook.usecases.reading.gateway.ReadingDAO;
import br.edu.ifsp.thebook.web.model.reading.request.ReadingUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReadingCRUDlmpl implements ReadingCRUD{

    private final ReadingDAO readingDAO;

    public ReadingCRUDlmpl(ReadingDAO readingDAO) {
        this.readingDAO = readingDAO;
    }

    @Override
    public Reading registerNewReading(UUID idUser, UUID idBook) {
        Reading reading = new Reading(idUser, idBook);
        return readingDAO.addNewReading(reading);
    }

    @Override
    public List<Reading> getAllReading() {
        return readingDAO.findAll();
    }

    @Override
    public Reading findReading(UUID idUser, UUID idBook) {
        return readingDAO.findByUserIdAndBookId(idUser, idBook);
    }

    @Override
    public Reading updateFavorite(UUID idUser, UUID idBook) {
        Reading reading = readingDAO.findByUserIdAndBookId(idUser, idBook);
        return readingDAO.updateFavorite(reading);
    }

    @Override
    public Reading updateState(UUID idUser, UUID idBook, ReadingUpdateRequest request) {
        Reading reading = readingDAO.findByUserIdAndBookId(idUser, idBook);
        reading.setReadingState(request.getReadingState());

        return readingDAO.updateState(reading);
    }
}
