class handleOperations:
    def __init__(self):
        self.operations = {
            "register": self.add,
            "login": self.subtract,
            "arduino": self.multiply
        }

    def add(self, a, b):
        return a + b

    def subtract(self, a, b):
        return a - b

    def multiply(self, a, b):
        return a * b

    def divide(self, a, b):
        return a / b

    def handleOperation(self, operation, a, b):
        return self.operations[operation](a, b)