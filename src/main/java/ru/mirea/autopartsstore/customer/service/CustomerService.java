package ru.mirea.autopartsstore.customer.service;

import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.common.exception.ResourceNotFoundException;
import ru.mirea.autopartsstore.customer.dto.CustomerRequest;
import ru.mirea.autopartsstore.customer.dto.CustomerResponse;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.customer.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse findById(Long id) {
        Customer customer = getById(id);

        return toResponse(customer);
    }

    public CustomerResponse create(CustomerRequest request) {

        Customer customer = new Customer();

        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());

        Customer savedCustomer =
                customerRepository.save(customer);

        return toResponse(savedCustomer);
    }

    public CustomerResponse update(
            Long id,
            CustomerRequest request
    ) {

        Customer customer = getById(id);

        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());

        Customer updatedCustomer =
                customerRepository.save(customer);

        return toResponse(updatedCustomer);
    }

    public void delete(Long id) {

        Customer customer = getById(id);

        customerRepository.delete(customer);
    }

    private Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer with id " + id + " not found"
                        )
                );
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail()
        );
    }
}
