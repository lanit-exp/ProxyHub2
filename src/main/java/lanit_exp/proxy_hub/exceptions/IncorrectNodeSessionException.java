package lanit_exp.proxy_hub.exceptions;

public class IncorrectNodeSessionException extends RuntimeException{

    public IncorrectNodeSessionException() {
        super("[ NODE REGISTER ERROR ] Отсутствует параметр node_session, невозможно зарегистрировать подключение.");
    }

}
