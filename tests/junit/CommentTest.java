package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.Comment;

public class CommentTest extends TestCase {
    public void testComment() {
        Assert.assertNull(Comment.methodWithComment());
    }
}