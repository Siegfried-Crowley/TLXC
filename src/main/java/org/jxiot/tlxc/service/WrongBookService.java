package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.WrongBook;
import org.jxiot.tlxc.mapper.WrongBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WrongBookService {

    @Autowired
    private WrongBookMapper wrongBookMapper;

    public void addToWrongBook(Integer userId, Integer problemId, Integer submissionId) {
        WrongBook wrongBook = wrongBookMapper.findByUserIdAndProblemId(userId, problemId);

        if (wrongBook == null) {
            wrongBook = new WrongBook();
            wrongBook.setUserId(userId);
            wrongBook.setProblemId(problemId);
            wrongBook.setLastSubmissionId(submissionId);
            wrongBook.setStatus("unmastered");
            wrongBook.setWrongCount(1);
            wrongBookMapper.insert(wrongBook);
        } else {
            wrongBookMapper.updateWrongCount(userId, problemId, submissionId);
        }
    }

    public List<WrongBook> getWrongBooks(Integer userId, String status) {
        return wrongBookMapper.findByUserIdAndStatus(userId, status);
    }

    public void markAsMastered(Integer wrongBookId) {
        WrongBook wrongBook = new WrongBook();
        wrongBook.setId(wrongBookId);
        wrongBook.setStatus("mastered");
        wrongBookMapper.updateStatus(wrongBook);
    }
}
