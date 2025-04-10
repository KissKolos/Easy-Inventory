/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventoryapi;

import java.io.IOException;

/**
 *
 * @author 3041TAN-08
 */
public interface HTTPConnector {
    APIResponse execute(String method,String url,String token,String body,boolean readResponseBody) throws IOException;
}
