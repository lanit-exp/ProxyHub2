package lanit_exp.proxy_hub.exceptions;

public class NodeIdAlreadyExistException extends RuntimeException {

    public NodeIdAlreadyExistException(String id) {
        super(("Клиент с id: '%s' уже зарегистрирован на сервере. " +
                "Измените параметр node_id и повторите попытку подключения").formatted(id));
    }
}
