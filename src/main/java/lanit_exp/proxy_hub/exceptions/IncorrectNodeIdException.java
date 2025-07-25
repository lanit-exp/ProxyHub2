package lanit_exp.proxy_hub.exceptions;

public class IncorrectNodeIdException extends RuntimeException{

    public IncorrectNodeIdException() {
        super("Отсутствует параметр node_id, невозможно зарегистрировать подключение");
    }

}
