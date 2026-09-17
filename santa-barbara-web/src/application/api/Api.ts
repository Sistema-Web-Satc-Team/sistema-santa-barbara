
class Api {
    private static root: string = "http://localhost:8080/api/v1/" 

    public static getRooutResource(): string {
        return Api.root;
    }

    public static getAuthResource(): string {
        return Api.root + "auth/";
    }
}

export { Api };
