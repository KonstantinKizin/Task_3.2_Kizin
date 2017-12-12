package by.online.pharmacy.service.impl;

import by.online.pharmacy.dao.CustomerDAO;
import by.online.pharmacy.dao.exception.DAOException;
import by.online.pharmacy.dao.factory.DAOFactory;
import by.online.pharmacy.entity.model.Customer;
import by.online.pharmacy.service.CustomerService;
import by.online.pharmacy.service.exception.ServiceException;
import org.apache.log4j.Logger;

import javax.servlet.ServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final DAOFactory factory = DAOFactory.getInstance();
    private final CustomerDAO customerDAO = factory.getCustomerDao();
    private final static Logger logger = Logger.getLogger(CustomerServiceImpl.class);
    private final String EMAIL_REQUEST_PARAMETER = "email";
    private final String PW_REQUEST_PARAMETER = "password";
    private final  char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };
    private final String ALGHORITHM = "SHA-1";

    @Override
    public boolean saveCustomer(Customer customer) throws ServiceException {
        try {
            return customerDAO.save(customer);
        } catch (DAOException e) {
            logger.debug("Exception from Service , saveCustomer method",e);
            throw new ServiceException(e);
        }
    }

    @Override
    public Customer findCustomerByEmailAndPassword(String email, String password) throws ServiceException {
        try {
            return customerDAO.findCustomerByEmailAndPw(email,password);
        } catch (DAOException e) {
            logger.error("Exception from Service , findCustomerByEmailAndPassword method",e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Customer> getAllCustomers() throws ServiceException {
        try {
            return customerDAO.getAll();
        } catch (DAOException e) {
            logger.error("Exception From Service in getAllCustomers method ",e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean LoginValidate(ServletRequest request){

        String email = request.getParameter(EMAIL_REQUEST_PARAMETER);
        String pw = request.getParameter(PW_REQUEST_PARAMETER);

        if(email != null && pw != null) {
            if (email.isEmpty() || pw.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String generateHashPassword(String pw) throws ServiceException {
        StringBuilder hash = new StringBuilder();
        try {
            MessageDigest sha = MessageDigest.getInstance(ALGHORITHM);
            byte[] hashedBytes = sha.digest(pw.getBytes());
            for (int idx = 0; idx < hashedBytes.length; ++idx) {
                byte b = hashedBytes[idx];
                hash.append(DIGITS[(b & 0xf0) >> 4]);
                hash.append(DIGITS[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Exception from creating hash password");
        }

        return hash.toString();
    }


}
