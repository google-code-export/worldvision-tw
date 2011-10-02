class Letter
  include DataMapper::Resource
  property :id, Serial
  property :create_date, Date
  property :employee_id, Text
  property :country, Text
  property :type, Text
  property :note, Text
  property :status, Text
  property :number_of_letters, Integer
  property :voulenteer_id, Text
  property :voulenteer_name, Text
  property :claim_date, Date
  property :due_date, Date
  property :return_date, Date
  property :upload_file, Text
  property :upload_file_url, Text
  property :return_file, Text
  property :return_file_url, Text
  
  def self.upload_file(upload)
      Letter.create(:upload_file=>upload)
  end
  def upload_file
      Letter.create(:upload_file=>@file)
  end
end

