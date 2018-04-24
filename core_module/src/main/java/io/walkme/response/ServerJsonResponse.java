package io.walkme.response;

/**
 * Server response interface to the client.
 * Example 1:
 *  {
 *      "status": 200,
 *      "error": "some error message"
 *  }
 *
 * Example 2:
 *  {
 *      "status": 200,
 *      "result": {
 *          ...
 *      }
 *  }
 *
 *  The result and error can't be together.
 */
public interface ServerJsonResponse<T, E, P> {
    /**
     * Set HTTP status code or something else
     * @param status representation of status
     */
    void setStatus(int status);

    /**
     * Set JSON result as a E type
     * @param result representation of result
     */
    void setResult(E result) throws IllegalStateException;

    /**
     * Set error message as a P type
     * @param error representation of error
     */
    void setError(P error) throws IllegalStateException;

    /**
     * @return T as result object
     */
    T getResult() throws IllegalStateException;
}
