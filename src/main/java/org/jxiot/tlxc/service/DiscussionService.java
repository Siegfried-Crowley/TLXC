package org.jxiot.tlxc.service;

import org.jxiot.tlxc.entity.Comment;
import org.jxiot.tlxc.entity.Discussion;
import org.jxiot.tlxc.mapper.CommentMapper;
import org.jxiot.tlxc.mapper.DiscussionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionMapper discussionMapper;

    @Autowired
    private CommentMapper commentMapper;

    public List<Discussion> getDiscussionsByProblem(Integer problemId, String type) {
        List<Discussion> list = discussionMapper.findByProblemIdAndType(problemId, type);
        for (Discussion d : list) {
            List<Comment> comments = commentMapper.findByDiscussionId(d.getId());
            for (Comment c : comments) {
                c.setReplies(commentMapper.findRepliesByParentId(c.getId()));
            }
            d.setComments(comments);
        }
        return list;
    }

    public Discussion getDiscussionById(Integer id) {
        Discussion d = discussionMapper.findById(id);
        if (d != null) {
            discussionMapper.incrementViewCount(id);
            List<Comment> comments = commentMapper.findByDiscussionId(id);
            for (Comment c : comments) {
                c.setReplies(commentMapper.findRepliesByParentId(c.getId()));
            }
            d.setComments(comments);
        }
        return d;
    }

    @Transactional
    public Discussion createDiscussion(Discussion discussion) {
        discussionMapper.insert(discussion);
        return discussion;
    }

    @Transactional
    public Comment addComment(Comment comment) {
        commentMapper.insert(comment);
        discussionMapper.incrementCommentCount(comment.getDiscussionId());
        return comment;
    }

    @Transactional
    public void deleteDiscussion(Integer id) {
        commentMapper.deleteByDiscussionId(id);
        discussionMapper.deleteById(id);
    }

    @Transactional
    public void deleteComment(Integer id) {
        Comment c = commentMapper.findById(id);
        if (c != null) {
            commentMapper.deleteById(id);
        }
    }

    public List<Discussion> getRecentSolutions(int limit) {
        return discussionMapper.findRecentByType("solution", limit);
    }
}
