package no.ion.hwmonitor.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

public class Exceptions {
    public interface SupplierThrowingException<T, E extends Exception> {
        T get() throws E;
    }

    public static <T, E extends Exception> T uncheck(SupplierThrowingException<T, E> supplier) {
        return uncheck(supplier, RuntimeException::new);
    }

    public static <T, E extends IOException> T uncheckIO(SupplierThrowingException<T, E> supplier) {
        return uncheck(supplier, UncheckedIOException::new);
    }

    public static <T, E extends Exception> T uncheck(SupplierThrowingException<T, E> supplier,
                                                     Function<E, RuntimeException> ExceptionToRuntimeExceptionMapper) {
        try {
            return supplier.get();
        } catch (Exception e) {
            // This cast is known to succeed
            @SuppressWarnings("unchecked")
            E a = (E) e;
            throw ExceptionToRuntimeExceptionMapper.apply(a);
        }
    }

    public interface RunnableThrowingException<E extends Exception> {
        void get() throws E;
    }

    public static <E extends Exception> void uncheck(RunnableThrowingException<E> runnable) {
        uncheck(runnable, RuntimeException::new);
    }

    public static <E extends IOException> void uncheckIO(RunnableThrowingException<E> runnable) {
        uncheck(runnable, UncheckedIOException::new);
    }

    public static <E extends Exception> void uncheck(RunnableThrowingException<E> runnable,
                                                     Function<E, RuntimeException> ExceptionToRuntimeExceptionMapper) {
        try {
            runnable.get();
        } catch (Exception e) {
            // This cast is known to succeed
            @SuppressWarnings("unchecked")
            E a = (E) e;
            throw ExceptionToRuntimeExceptionMapper.apply(a);
        }
    }
}
