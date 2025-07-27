package lanit_exp.proxy_hub.exceptions;

public class IncorrectNodeIdException extends RuntimeException{

    public IncorrectNodeIdException() {
        super("[ NODE REGISTER ERROR ] Отсутствует параметр node_id, невозможно зарегистрировать подключение.");
    }

}
