import org.example.Arena;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArenaTest {
    @Test
    public void can_allocate_span_and_use_memory(){
        try( var arena = new Arena()){
            var buffer = arena.allocate(100, Byte.class);

            for(byte i = 0; i < buffer.limit(); i++)
                buffer.put(i);
        }
    }
    @Test
    public void can_allocate_float_span_and_use_memory()
    {
        try( var arena = new Arena()){
            var buffer = arena.allocate(100, Float.class);

            for(int i = 0; i < buffer.limit()/Float.BYTES; i++)
                buffer.putFloat(i);
        }
    }
    @Test
    public void should_fail_to_allocate_on_disposed_allocate()
    {
        try(var arena = new Arena()){
            arena.dispose();
            assertThrows(IllegalStateException.class,() -> arena.allocate(100,Float.class));
        }
    }
    @Test
    public void should_take_into_account_type_sizes()
    {
        try(var arena = new Arena(101)){
            //the initial size is in bytes, but memory needed for 100 floats is much more than 101 bytes...
            assertThrows(OutOfMemoryError.class ,() -> arena.allocate(100,Float.class));
        }
    }
    @Test
    public void should_fail_to_allocateBytes_beyond_size()
    {
        try(var arena = new Arena(1024)){
            var segment1 = arena.allocate(1000,Byte.class);
            var segment2 = arena.allocate(24,Byte.class);

            assertThrows(OutOfMemoryError.class,() -> arena.allocate(1,Byte.class));
        }
    }

    @Test
    public void Should_fail_to_tryallocate_beyond_size()
    {
       try(var arena = new Arena(1025)){
           var segment1 = arena.allocate(1000,Byte.class);
           var segment2 = arena.allocate(24,Byte.class);
           assertTrue(arena.tryAllocate(1,Byte.class));
       }


    }

    @Test
    public void reset_should_increment_ResetCount()
    {
        try(var arena = new Arena(1024)){
        arena.reset();
        arena.reset();
        assertEquals(2, arena.getResetCount());
    }
}
}
